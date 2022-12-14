package mcts_spacegame.model_mcts;

import common.Conditionals;
import common.CpuTimer;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.java.Log;
import mcts_spacegame.enums.Action;
import mcts_spacegame.environment.Environment;
import mcts_spacegame.environment.StepReturn;
import mcts_spacegame.exceptions.StartStateIsTrapException;
import mcts_spacegame.helpers.NodeInfoHelper;
import mcts_spacegame.helpers.TreeInfoHelper;
import mcts_spacegame.models_mcts_nodes.NodeInterface;
import mcts_spacegame.models_space.State;
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
public class MonteCarloTreeCreator {
    private static final double VALUE_MEMORY_IF_NOT_TERMINAL = 0d;
    Environment environment;
    State startState;
    MonteCarloSettings settings;
    NodeValueMemory memory;

    NodeInterface nodeRoot;
    TreeInfoHelper tih;
    CpuTimer cpuTimer;
    int nofIterations;
    List<Action> actionsToSelected;

    @Builder
    private static MonteCarloTreeCreator newMCTC(@NonNull Environment environment,
                                                 @NonNull State startState,
                                                 MonteCarloSettings monteCarloSettings,
                                                 NodeValueMemory memory) {
        MonteCarloTreeCreator mctc = new MonteCarloTreeCreator();
        mctc.environment = environment;
        mctc.startState = startState;
        mctc.settings = monteCarloSettings;

        Conditionals.executeOneOfTwo(Objects.isNull(monteCarloSettings),
                () -> mctc.settings = MonteCarloSettings.newDefault(),
                () -> mctc.settings = monteCarloSettings);

        Conditionals.executeOneOfTwo(Objects.isNull(memory),
                () -> mctc.memory = NodeValueMemory.newEmpty(),
                () -> mctc.memory = memory);

        setSomeFields(startState, mctc);
        return mctc;
    }

    private static void setSomeFields(@NonNull State startState, MonteCarloTreeCreator mctc) {
        mctc.nodeRoot = NodeInterface.newNotTerminal(startState, Action.notApplicable);
        mctc.tih = new TreeInfoHelper(mctc.nodeRoot,mctc.settings);
        mctc.cpuTimer = new CpuTimer(mctc.settings.timeBudgetMilliSeconds);
        mctc.nofIterations = 0;
    }

    /**
     *
     */

    public NodeInterface runIterations() throws StartStateIsTrapException {
        setSomeFields(startState, this);  //needed because setStartState will not effect correctly otherwise
        int i;
        ActionSelector actionSelector = new ActionSelector(settings);
        for (i = 0; i < settings.maxNofIterations; i++) {
            NodeInterface nodeSelected = select(nodeRoot);
            Optional<Action> actionInSelected = actionSelector.select(nodeSelected);
            if (actionInSelected.isPresent()) {
                StepReturn sr = applyActionAndExpand(nodeSelected, actionInSelected.get());
                SimulationResults simulationResults = simulate(sr.newPosition);
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

    private NodeInterface select(NodeInterface nodeRoot) {
        NodeSelector ns = new NodeSelector(nodeRoot, settings);
        NodeInterface nodeSelected = ns.select();
        actionsToSelected = ns.getActionsFromRootToSelected();
        return nodeSelected;
    }

    private StepReturn applyActionAndExpand(NodeInterface nodeSelected, Action actionInSelected) {
        State state = TreeInfoHelper.getState(startState, environment, actionsToSelected);
        StepReturn sr = environment.step(actionInSelected, state);
        nodeSelected.saveRewardForAction(actionInSelected, sr.reward);
        NodeInterface child = NodeInterface.newNode(sr, actionInSelected);
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

    public SimulationResults simulate(State stateAfterApplyingActionInSelectedNode) {
        SimulationResults simulationResults = new SimulationResults();
        for (int i = 0; i < settings.nofSimulationsPerNode; i++) {
            List<StepReturn> returns = stepToTerminal(stateAfterApplyingActionInSelectedNode.copy(), settings.simulationPolicy);
            StepReturn endReturn = returns.get(returns.size() - 1);
            double sumOfRewards = returns.stream().mapToDouble(r -> r.reward).sum();
            double valueInTerminal = memory.read(endReturn.newPosition);
            boolean isEndingInFail = endReturn.isFail;
            simulationResults.add(sumOfRewards, valueInTerminal * settings.weightMemoryValue, isEndingInFail);
        }
        return simulationResults;
    }

    private void backPropagate(StepReturn sr,
                               SimulationResults simulationResults,
                               Action actionInSelected) {
        SimulationReturnsExtractor bumSim = SimulationReturnsExtractor.builder()
                .nofNodesOnPath(actionsToSelected.size() + 1)
                .simulationResults(simulationResults)
                .settings(settings)
                .build();
        List<Double> returnsSimulation = bumSim.backup();

        double valueInTerminal = (sr.isTerminal)
                ? memory.read(sr.newPosition)
                : VALUE_MEMORY_IF_NOT_TERMINAL;

        BackupModifier bumSteps = BackupModifier.builder().rootTree(nodeRoot)
                .actionsToSelected(actionsToSelected)
                .actionOnSelected(actionInSelected)
                .stepReturnOfSelected(sr)
                .valueInTerminal(valueInTerminal)
                .settings(settings)
                .build();
        bumSteps.backup(returnsSimulation);

    }

    private void manageCaseWhenAllActionsAreTested(NodeInterface nodeSelected) throws StartStateIsTrapException {
        SelectedToTerminalFailConverter sfc = new SelectedToTerminalFailConverter(nodeRoot, actionsToSelected);
        if (sfc.areAllChildrenToSelectedNodeTerminalFail(nodeSelected)) {
            makeSelectedTerminal(nodeSelected, sfc);
        } else {
            chooseBestActionAndBackPropagate(nodeSelected);
        }
    }

    private void chooseBestActionAndBackPropagate(NodeInterface nodeSelected) {
        NodeSelector nodeSelector = new NodeSelector(nodeRoot,settings);
        Optional<NodeInterface> childToSelected = nodeSelector.selectChild(nodeSelected);
        Action actionToGetToChild = childToSelected.orElseThrow().getAction();
        State state = TreeInfoHelper.getState(startState, environment, actionsToSelected);
        StepReturn sr = environment.step(actionToGetToChild, state);
        backPropagate(sr, new SimulationResults(), actionToGetToChild);
    }

    private void makeSelectedTerminal(NodeInterface nodeSelected, SelectedToTerminalFailConverter sfc) throws StartStateIsTrapException {
        if (nodeSelected.equals(nodeRoot)) {
            nodeRoot.printTree();
            throw new StartStateIsTrapException("All children to root node are terminal - no solution exists");
        }
        sfc.makeSelectedTerminal(nodeSelected);
    }

    private List<StepReturn> stepToTerminal(State pos, SimulationPolicyInterface policy) {
        List<StepReturn> returns = new ArrayList<>();
        StepReturn stepReturn;
        do {
            Action action = policy.chooseAction(pos);
            stepReturn = environment.step(action, pos);
            pos.setFromReturn(stepReturn);
            returns.add(stepReturn);
        } while (!stepReturn.isTerminal);
        return returns;
    }

}
