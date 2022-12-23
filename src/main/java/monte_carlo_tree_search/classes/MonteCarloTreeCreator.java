package monte_carlo_tree_search.classes;

import common.Conditionals;
import common.CpuTimer;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.java.Log;
import monte_carlo_tree_search.domains.cart_pole.ActionCartPole;
import monte_carlo_tree_search.domains.cart_pole.CartPoleVariables;
import monte_carlo_tree_search.exceptions.StartStateIsTrapException;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.generic_interfaces.StateInterface;
import monte_carlo_tree_search.helpers.NodeInfoHelper;
import monte_carlo_tree_search.helpers.TreeInfoHelper;
import monte_carlo_tree_search.node_models.NodeInterface;
import monte_carlo_tree_search.generic_interfaces.SimulationPolicyInterface;

import java.util.*;

/***
 *   This class performs monte carlo tree search
 *
 *   Two vectors: List<Double> returnsSteps, List<Double> returnsSimulation, plays a central role
 *   One of them returnsSteps is derived from chooseActionAndExpand(). The other, returnsSimulation, is from simulate().
 *
 *   Assume no weighting and the following example settings: returnsSteps=[-2,-1,0], returnsSimulation=[6,6,6]
 *   The values in the nodes of the selection path will be modified according to the sum of the vectors, i.e. [4,5,6]
 *
 *   Depending on the properties of the action in selected node following logic is applied
 *
 *   actionInSelected is present (there is a valid action)
 *      => applyActionAndExpand, simulate, backPropagate
 *   actionInSelected is empty & AllChildrenInSelectedAreFail  (all actions are tested and leads to fail node)
 *      =>  convertSelectedNodeToFail
 *   actionInSelected is empty & not AllChildrenInSelectedAreFail  (all actions are tested but some is not fail)
 *      => actionInSelected = nodeSelector.selectChild(), stepReturn=applyAction(actionInSelected), backPropagate(stepReturn)
 *
 *  The parameter actionTemplate is needed as a "seed" to create a tree with unknown types.
 */

@Log
@Setter
@Getter
public class MonteCarloTreeCreator<SSV,AV> {
    private static final double VALUE_MEMORY_IF_NOT_TERMINAL = 0d;
    EnvironmentGenericInterface<SSV, AV> environment;
    StateInterface<SSV> startState;
    MonteCarloSettings<SSV,AV> settings;
    ActionInterface<AV> actionTemplate;
    NodeValueMemory<SSV> memory;

    NodeInterface<SSV,AV> nodeRoot;
    TreeInfoHelper<SSV,AV> tih;
    CpuTimer cpuTimer;
    int nofIterations;
    List<ActionInterface<AV>> actionsToSelected;

    @Builder
    private static <SSV,AV> MonteCarloTreeCreator<SSV,AV>  newMCTC(
                                                 @NonNull EnvironmentGenericInterface<SSV, AV> environment,
                                                 @NonNull StateInterface<SSV> startState,
                                                 @NonNull MonteCarloSettings<SSV,AV> monteCarloSettings,
                                                 @NonNull ActionInterface<AV> actionTemplate,
                                                 NodeValueMemory<SSV> memory) {
        MonteCarloTreeCreator<SSV,AV> mctc = new MonteCarloTreeCreator<>();
        mctc.environment = environment;
        mctc.startState = startState;
        mctc.settings = monteCarloSettings;

        mctc.actionTemplate=actionTemplate;
        Conditionals.executeOneOfTwo(Objects.isNull(memory),
                () -> mctc.memory = NodeValueMemory.newEmpty(),
                () -> mctc.memory = memory);

        setSomeFields(startState, mctc);
        return mctc;
    }

    private static <SSV,AV>  void setSomeFields(@NonNull StateInterface<SSV> startState, MonteCarloTreeCreator<SSV,AV>  mctc) {
        ActionInterface<AV> actionRoot=mctc.actionTemplate.copy();
        actionRoot.setValue(actionRoot.nonApplicableAction());
        mctc.nodeRoot = NodeInterface.newNotTerminal(startState,actionRoot);
        mctc.tih = new TreeInfoHelper<>(mctc.nodeRoot, mctc.settings);
        mctc.cpuTimer = new CpuTimer(mctc.settings.timeBudgetMilliSeconds);
        mctc.nofIterations = 0;
    }

    public NodeInterface<SSV,AV> run() throws StartStateIsTrapException {
        setSomeFields(startState, this);  //needed because setStartState will not effect correctly otherwise
        int i;
        ActionSelector<SSV,AV> actionSelector = new ActionSelector<>(settings,actionTemplate);
        for (i = 0; i < settings.maxNofIterations; i++) {
            NodeInterface<SSV,AV> nodeSelected = select(nodeRoot);
            Optional<ActionInterface<AV>> actionInSelected = actionSelector.select(nodeSelected);
            if (actionInSelected.isPresent()) {
                StepReturnGeneric<SSV> sr = applyActionAndExpand(nodeSelected, actionInSelected.get());
                SimulationResults simulationResults = simulate(sr.newState);
                backPropagate(sr, simulationResults, actionInSelected.get());
            } else {  // actionInSelected is empty <=> all tested
                manageCaseWhenAllActionsAreTested(nodeSelected);
            }

            if (cpuTimer.isTimeExceeded()) {
                log.fine("Time exceeded");
                break;
            }
        }
        nofIterations = i;
        this.cpuTimer.stop();
        log.info("time used = " + cpuTimer.getAbsoluteProgress() + ", nofIterations = " + nofIterations);

        return nodeRoot;
    }

    public MonteCarloSearchStatistics<SSV,AV> getStatistics() {
        MonteCarloSearchStatistics<SSV,AV> statistics = new MonteCarloSearchStatistics<>(nodeRoot, this,settings);
        statistics.setStatistics();
        return statistics;
    }

    public ActionInterface<AV> getFirstAction() {
        TreeInfoHelper<SSV, AV> tih = new TreeInfoHelper<>(nodeRoot,settings);
        ActionInterface<AV> actionRoot=actionTemplate.copy();
        AV actionValue=tih.getValueOfFirstBestAction().orElseThrow();
        actionRoot.setValue(actionValue);
        return actionRoot;
    }

    private NodeInterface<SSV,AV> select(NodeInterface<SSV,AV> nodeRoot) {
        NodeSelector<SSV,AV> ns = new NodeSelector<>(nodeRoot, settings);
        NodeInterface<SSV,AV> nodeSelected = ns.select();
        actionsToSelected = ns.getActionsFromRootToSelected();
        return nodeSelected;
    }

    private StepReturnGeneric<SSV> applyActionAndExpand(NodeInterface<SSV,AV> nodeSelected, ActionInterface<AV> actionInSelected) {
        StateInterface<SSV> state = TreeInfoHelper.getState(startState, environment, actionsToSelected);
        StepReturnGeneric<SSV> sr = environment.step(actionInSelected, state);
        nodeSelected.saveRewardForAction(actionInSelected, sr.reward);
        NodeInterface<SSV,AV> child = NodeInterface.newNode(sr, actionInSelected);
        child.setDepth(nodeSelected.getDepth() + 1);  //easy to forget
        boolean isChildAddedEarlier = NodeInfoHelper.findNodeMatchingNode(nodeSelected.getChildNodes(), child).isPresent();
        boolean isSelectedNotTerminal = nodeSelected.isNotTerminal();
        boolean isChildToDeep = child.getDepth() > settings.maxTreeDepth;

        Conditionals.executeIfTrue(!isChildOkToAdd(isChildAddedEarlier, isChildToDeep), () ->
                log.fine("Child will not be added, child = " + child.getName() + ", in node = " + nodeSelected.getName()
                        + ", isChildAddedEarlier =" + isChildAddedEarlier + ", isChildToDeep =" + isChildToDeep));

        Conditionals.executeIfTrue(isSelectedNotTerminal &&
                isChildOkToAdd(isChildAddedEarlier, isChildToDeep), () ->
                nodeSelected.addChildNode(child));
        return sr;
    }

    private boolean isChildOkToAdd(boolean isChildAddedEarlier, boolean isChildToDeep) {
        return !isChildAddedEarlier && !isChildToDeep;
    }

    //todo apply discountFactorSimulation
    public SimulationResults simulate(StateInterface<SSV> stateAfterApplyingActionInSelectedNode) {
        SimulationResults simulationResults = new SimulationResults();
        for (int i = 0; i < settings.nofSimulationsPerNode; i++) {
            List<StepReturnGeneric<SSV>> returns =
                    stepToTerminal(stateAfterApplyingActionInSelectedNode.copy(), settings.simulationPolicy);
            StepReturnGeneric<SSV> endReturn = returns.get(returns.size() - 1);
            double sumOfRewards = returns.stream().mapToDouble(r -> r.reward).sum();
            double valueInTerminal = memory.read(endReturn.newState);
            boolean isEndingInFail = endReturn.isFail;
            simulationResults.add(sumOfRewards, valueInTerminal * settings.weightMemoryValue, isEndingInFail);
        }
        return simulationResults;
    }

    private void backPropagate(StepReturnGeneric<SSV> sr,
                               SimulationResults simulationResults,
                               ActionInterface<AV> actionInSelected) {
        SimulationReturnsExtractor<SSV,AV> bumSim = SimulationReturnsExtractor.<SSV,AV>builder()
                .nofNodesOnPath(actionsToSelected.size() + 1)
                .simulationResults(simulationResults)
                .settings(settings)
                .build();
        List<Double> returnsSimulation = bumSim.extract();

        double valueInTerminal = (sr.isTerminal)
                ? memory.read(sr.newState)
                : VALUE_MEMORY_IF_NOT_TERMINAL;

        BackupModifier<SSV, AV>  bumSteps = BackupModifier.<SSV, AV> builder().rootTree(nodeRoot)
                .actionsToSelected(actionsToSelected)
                .actionOnSelected(actionInSelected)
                .stepReturnOfSelected(sr)
                .valueInTerminal(valueInTerminal)
                .settings(settings)
                .build();
        bumSteps.backup(returnsSimulation);
    }

    private void manageCaseWhenAllActionsAreTested(NodeInterface<SSV, AV>  nodeSelected) throws StartStateIsTrapException {
        SelectedToTerminalFailConverter<SSV, AV>  sfc = new SelectedToTerminalFailConverter<>(nodeRoot, actionsToSelected);
        if (sfc.areAllChildrenToSelectedNodeTerminalFail(nodeSelected)) {
            makeSelectedTerminal(nodeSelected, sfc);
        } else {
            chooseBestActionAndBackPropagate(nodeSelected);
        }
    }

    private void chooseBestActionAndBackPropagate(NodeInterface<SSV, AV>  nodeSelected) {
        NodeSelector<SSV, AV>  nodeSelector = new NodeSelector<>(nodeRoot,settings);
        Optional<NodeInterface<SSV, AV> > childToSelected = nodeSelector.selectChild(nodeSelected);
        ActionInterface<AV> actionToGetToChild = childToSelected.orElseThrow().getAction();
        StateInterface<SSV> state = TreeInfoHelper.getState(startState, environment, actionsToSelected);
        StepReturnGeneric<SSV> sr = environment.step(actionToGetToChild, state);
        backPropagate(sr, new SimulationResults(), actionToGetToChild);
    }

    private void makeSelectedTerminal(NodeInterface<SSV, AV> nodeSelected, SelectedToTerminalFailConverter<SSV, AV> sfc) throws StartStateIsTrapException {
        if (nodeSelected.equals(nodeRoot)) {
            nodeRoot.printTree();
            throw new StartStateIsTrapException("All children to root node are terminal - no solution exists");
        }
        sfc.makeSelectedTerminal(nodeSelected);
    }

    private List<StepReturnGeneric<SSV>> stepToTerminal(StateInterface<SSV> pos, SimulationPolicyInterface<SSV, AV> policy) {
        List<StepReturnGeneric<SSV>> returns = new ArrayList<>();
        StepReturnGeneric<SSV> stepReturn;
        do {
            ActionInterface<AV> action = policy.chooseAction(pos);
            stepReturn = environment.step(action, pos);
            pos.setFromReturn(stepReturn);
            returns.add(stepReturn);
        } while (!stepReturn.isTerminal);
        return returns;
    }

}
