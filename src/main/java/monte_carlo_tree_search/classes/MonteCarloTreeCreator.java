package monte_carlo_tree_search.classes;

import common.Conditionals;
import common.CpuTimer;
import common.ListUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.java.Log;
import monte_carlo_tree_search.exceptions.StartStateIsTrapException;
import monte_carlo_tree_search.generic_interfaces.*;
import monte_carlo_tree_search.helpers.NodeInfoHelper;
import monte_carlo_tree_search.helpers.TreeInfoHelper;
import monte_carlo_tree_search.node_models.NodeInterface;

import java.util.*;
import java.util.stream.Collectors;

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
public class MonteCarloTreeCreator<S,A> {
    private static final double VALUE_MEMORY_IF_NOT_TERMINAL = 0d;
    EnvironmentGenericInterface<S, A> environment;
    StateInterface<S> startState;
    MonteCarloSettings<S,A> settings;
    ActionInterface<A> actionTemplate;
    MemoryInterface<S> memory;

    NodeInterface<S,A> nodeRoot;
    TreeInfoHelper<S,A> tih;
    CpuTimer cpuTimer;
    int nofIterations;
    List<ActionInterface<A>> actionsToSelected;
    List<Double> rootValueList;

    @Builder
    private static <S,A> MonteCarloTreeCreator<S,A>  newMCTC(
                                                 @NonNull EnvironmentGenericInterface<S, A> environment,
                                                 @NonNull StateInterface<S> startState,
                                                 @NonNull MonteCarloSettings<S,A> monteCarloSettings,
                                                 @NonNull ActionInterface<A> actionTemplate,
                                                 MemoryInterface<S> memory) {
        MonteCarloTreeCreator<S,A> mctc = new MonteCarloTreeCreator<>();
        mctc.environment = environment;
        mctc.startState = startState;
        mctc.settings = monteCarloSettings;

        mctc.actionTemplate=actionTemplate;
        Conditionals.executeOneOfTwo(Objects.isNull(memory),
                () -> mctc.memory = NodeValueMemoryHashMap.newEmpty(),
                () -> mctc.memory = memory);

        setSomeFields(startState, mctc);
        return mctc;
    }

    private static <S,A>  void setSomeFields(@NonNull StateInterface<S> startState, MonteCarloTreeCreator<S,A>  mctc) {
        ActionInterface<A> actionRoot=mctc.actionTemplate.copy();
        actionRoot.setValue(actionRoot.nonApplicableAction());
        mctc.nodeRoot = NodeInterface.newNotTerminal(startState,actionRoot);
        mctc.tih = new TreeInfoHelper<>(mctc.nodeRoot, mctc.settings);
        mctc.cpuTimer = new CpuTimer(mctc.settings.timeBudgetMilliSeconds);
        mctc.nofIterations = 0;
        mctc.rootValueList =new ArrayList<>();
    }

    public NodeInterface<S,A> run() throws StartStateIsTrapException {
        setSomeFields(startState, this);  //needed because setStartState will not effect correctly otherwise

        int i;
        rootValueList.clear();
        ActionSelector<S,A> actionSelector = new ActionSelector<>(settings,actionTemplate);
        for (i = 0; i < settings.maxNofIterations; i++) {
            NodeInterface<S,A> nodeSelected = select(nodeRoot);
            Optional<ActionInterface<A>> actionInSelected = actionSelector.select(nodeSelected);
            if (actionInSelected.isPresent()) {
                StepReturnGeneric<S> sr = applyActionAndExpand(nodeSelected, actionInSelected.get());
                SimulationResults simulationResults = simulate(sr.newState);
                backPropagate(sr, simulationResults, actionInSelected.get());
            } else {  // actionInSelected is empty <=> all tested
                manageCaseWhenAllActionsAreTested(nodeSelected);
            }

            updateRootvalueList();
            if (cpuTimer.isTimeExceeded()) {
                log.fine("Time exceeded");
                break;
            }
        }
        logStatistics(i);
        return nodeRoot;
    }

    private void updateRootvalueList() {
        List<Double> avs=new ArrayList<>();
        for(A a:actionTemplate.applicableActions()) {
            actionTemplate.setValue(a);
            avs.add(nodeRoot.getActionValue(actionTemplate));
        }
        rootValueList.add(ListUtils.findMax(avs).orElse(0));
    }


    public MonteCarloSearchStatistics<S,A> getStatistics() {
        MonteCarloSearchStatistics<S,A> statistics = new MonteCarloSearchStatistics<>(nodeRoot, this,settings);
        statistics.setStatistics();
        return statistics;
    }

    public ActionInterface<A> getFirstAction() {
        TreeInfoHelper<S, A> tih = new TreeInfoHelper<>(nodeRoot,settings);
        ActionInterface<A> actionRoot=actionTemplate.copy();
        Conditionals.executeIfTrue (tih.getValueOfFirstBestAction().isEmpty(), () ->
            log.warning("No first action present - probably to small time budget"));
        A actionValue=tih.getValueOfFirstBestAction().orElse(actionTemplate.getValue());
        actionRoot.setValue(actionValue);
        return actionRoot;
    }

    private void logStatistics(int nofIterations) {
        this.cpuTimer.stop();
        TreeInfoHelper<S,A> tih=new TreeInfoHelper<>(nodeRoot,settings);
        MonteCarloSearchStatistics<S,A> statistics=new MonteCarloSearchStatistics<>(
                nodeRoot,this,settings);
        log.fine("time used = " + cpuTimer.getAbsoluteProgress() + ", nofIterations = " + nofIterations+
                ", max tree depth = "+tih.maxDepth()+", depth of best path = "+tih.getBestPath().size()
                +", nof nodes = "+statistics.nofNodes+", branching = "+statistics.averageNofChildrenPerNode);
    }

    private NodeInterface<S,A> select(NodeInterface<S,A> nodeRoot) {
        NodeSelector<S,A> ns = new NodeSelector<>(nodeRoot, settings);
        NodeInterface<S,A> nodeSelected = ns.select();
        actionsToSelected = ns.getActionsFromRootToSelected();
        return nodeSelected;
    }

    private StepReturnGeneric<S> applyActionAndExpand(NodeInterface<S,A> nodeSelected, ActionInterface<A> actionInSelected) {
        StateInterface<S> state = TreeInfoHelper.getState(startState, environment, actionsToSelected);
        StepReturnGeneric<S> sr = environment.step(actionInSelected, state);
        nodeSelected.saveRewardForAction(actionInSelected, sr.reward);
        NodeInterface<S,A> child = NodeInterface.newNode(sr, actionInSelected);
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
    public SimulationResults simulate(StateInterface<S> stateAfterApplyingActionInSelectedNode) {
        SimulationResults simulationResults = new SimulationResults();
        for (int i = 0; i < settings.nofSimulationsPerNode; i++) {
            List<StepReturnGeneric<S>> stepResults =
                    stepToTerminal(stateAfterApplyingActionInSelectedNode.copy(), settings.simulationPolicy);
            StepReturnGeneric<S> endReturn = stepResults.get(stepResults.size() - 1);
          //  double sumOfRewards = stepResults.stream().mapToDouble(r -> r.reward).sum();
            double sumOfRewards = ListUtils.discountedSum(
                    stepResults.stream().map(r -> r.reward).collect(Collectors.toList()),
                    settings.discountFactorSimulation);
            boolean isEndingInFail = endReturn.isFail;
            simulationResults.add(sumOfRewards, isEndingInFail);
        }
        return simulationResults;
    }

    private void backPropagate(StepReturnGeneric<S> sr,
                               SimulationResults simulationResults,
                               ActionInterface<A> actionInSelected) {
        SimulationReturnsExtractor<S,A> bumSim = SimulationReturnsExtractor.<S,A>builder()
                .nofNodesOnPath(actionsToSelected.size() + 1)
                .simulationResults(simulationResults)
                .settings(settings)
                .build();
        List<Double> returnsSimulation = bumSim.extract();

        double memoryValueStateAfterAction=memory.read(sr.newState);
        BackupModifier<S, A>  bum = BackupModifier.<S, A> builder().rootTree(nodeRoot)
                .actionsToSelected(actionsToSelected)
                .actionOnSelected(actionInSelected)
                .stepReturnOfSelected(sr)
                .settings(settings)
                .build();
        bum.backup(returnsSimulation,memoryValueStateAfterAction);
    }

    private void manageCaseWhenAllActionsAreTested(NodeInterface<S, A>  nodeSelected) throws StartStateIsTrapException {
        SelectedToTerminalFailConverter<S, A>  sfc = new SelectedToTerminalFailConverter<>(nodeRoot, actionsToSelected);
        if (sfc.areAllChildrenToSelectedNodeTerminalFail(nodeSelected)) {
            makeSelectedTerminal(nodeSelected, sfc);
        } else {
            chooseBestActionAndBackPropagate(nodeSelected);
        }
    }

    private void chooseBestActionAndBackPropagate(NodeInterface<S, A>  nodeSelected) {
        NodeSelector<S, A>  nodeSelector = new NodeSelector<>(nodeRoot,settings);
        Optional<NodeInterface<S, A> > childToSelected = nodeSelector.selectChild(nodeSelected);
        ActionInterface<A> actionToGetToChild = childToSelected.orElseThrow().getAction();
        StateInterface<S> state = TreeInfoHelper.getState(startState, environment, actionsToSelected);
        StepReturnGeneric<S> sr = environment.step(actionToGetToChild, state);
        backPropagate(sr, new SimulationResults(), actionToGetToChild);
    }

    private void makeSelectedTerminal(NodeInterface<S, A> nodeSelected,
                                      SelectedToTerminalFailConverter<S, A> sfc) throws StartStateIsTrapException {
        if (nodeSelected.equals(nodeRoot)) {
           // nodeRoot.printTree();
            throw new StartStateIsTrapException("All children to root node are terminal - no solution exists");
        }
        sfc.makeSelectedTerminal(nodeSelected);
    }

    private List<StepReturnGeneric<S>> stepToTerminal(StateInterface<S> state,
                                                        SimulationPolicyInterface<S, A> policy) {
        List<StepReturnGeneric<S>> returns = new ArrayList<>();
        StepReturnGeneric<S> stepReturn;
        do {
            ActionInterface<A> action = policy.chooseAction(state);
            stepReturn = environment.step(action, state);
            state.setFromReturn(stepReturn);
            returns.add(stepReturn);
        } while (!stepReturn.isTerminal);
        return returns;
    }

}
