package mcts_spacegame.model_mcts;

import common.Conditionals;
import common.CpuTimer;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.java.Log;
import mcts_spacegame.environment.EnvironmentShip;
import mcts_spacegame.environment.StepReturnGeneric;
import mcts_spacegame.exceptions.StartStateIsTrapException;
import mcts_spacegame.generic_interfaces.ActionInterface;
import mcts_spacegame.generic_interfaces.EnvironmentGenericInterface;
import mcts_spacegame.generic_interfaces.StateInterface;
import mcts_spacegame.helpers.NodeInfoHelper;
import mcts_spacegame.helpers.TreeInfoHelper;
import mcts_spacegame.models_mcts_nodes.NodeInterface;
import mcts_spacegame.models_space.ActionShip;
import mcts_spacegame.models_space.ShipActionSet;
import mcts_spacegame.models_space.ShipVariables;
import mcts_spacegame.models_space.StateShip;
import mcts_spacegame.policies_action.SimulationPolicyInterface;

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
 */

@Log
@Setter
@Getter
public class MonteCarloTreeCreator<SSV,AV> {
    private static final double VALUE_MEMORY_IF_NOT_TERMINAL = 0d;
    EnvironmentGenericInterface<SSV, AV> environment;
    StateInterface<SSV> startState;
    MonteCarloSettings<SSV,AV> settings;
    ActionInterface<AV> actionTemplate;  //todo can we remove and use extend on AV instead?
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

    //    Conditionals.executeOneOfTwo(Objects.isNull(monteCarloSettings),
     //           () -> mctc.settings = MonteCarloSettings.newDefault(),
      //          () -> mctc.settings = monteCarloSettings);

        mctc.actionTemplate=actionTemplate;
        Conditionals.executeOneOfTwo(Objects.isNull(memory),
                () -> mctc.memory = NodeValueMemory.newEmpty(),
                () -> mctc.memory = memory);

        setSomeFields(startState, mctc);
        return mctc;
    }

    private static <SSV,AV>  void setSomeFields(@NonNull StateInterface<SSV> startState, MonteCarloTreeCreator<SSV,AV>  mctc) {
        mctc.nodeRoot = NodeInterface.newNotTerminal(startState,mctc.actionTemplate);  //todo generic not null
        mctc.tih = new TreeInfoHelper<>(mctc.nodeRoot, mctc.settings);
        mctc.cpuTimer = new CpuTimer(mctc.settings.timeBudgetMilliSeconds);
        mctc.nofIterations = 0;
    }


    public NodeInterface<SSV,AV> runIterations() throws StartStateIsTrapException {
        setSomeFields(startState, this);  //needed because setStartState will not effect correctly otherwise
        int i;
        ActionSelector<SSV,AV> actionSelector = new ActionSelector<>(settings);
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
                log.warning("Time exceeded");
                break;
            }
        }
        nofIterations = i;
        this.cpuTimer.stop();
        log.info("time used = " + cpuTimer.getAbsoluteProgress() + ", nofIterations = " + nofIterations);

        return nodeRoot;
    }

    public MonteCarloSearchStatistics getStatistics() {
        MonteCarloSearchStatistics statistics = new MonteCarloSearchStatistics(nodeRoot, cpuTimer, nofIterations,settings);
        statistics.setStatistics();
        return statistics;
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
        SimulationReturnsExtractor bumSim = SimulationReturnsExtractor.builder()
                .nofNodesOnPath(actionsToSelected.size() + 1)
                .simulationResults(simulationResults)
                .settings(settings)
                .build();
        List<Double> returnsSimulation = bumSim.simulate();

        double valueInTerminal = (sr.isTerminal)
                ? memory.read((StateInterface<SSV>) sr.newState)  //todo StateInterface<TYPE>
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
