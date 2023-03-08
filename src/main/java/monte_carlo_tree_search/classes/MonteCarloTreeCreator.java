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
import monte_carlo_tree_search.node_models.NodeWithChildrenInterface;

import java.util.*;
import java.util.stream.Collectors;

/***
 *   This class performs monte carlo tree search
 *   Two vectors: List<Double> returnsSteps, List<Double> returnsSimulation, plays a central role
 *   One of them returnsSteps is derived from chooseActionAndExpand(). The other, returnsSimulation, is from simulate().
 * <p>
 *   Assume no weighting and the following example settings: returnsSteps=[-2,-1,0], returnsSimulation=[6,6,6]
 *   The values in the nodes of the selection path will be modified according to the sum of the vectors, i.e. [4,5,6]
 * <p>
 *   Depending on the properties of the action in selected node following logic is applied
 * <p>
 *   actionInSelected is present (there is a valid action)
 *      => applyActionAndExpand, simulate, backPropagate
 *   actionInSelected is empty & AllChildrenInSelectedAreFail  (all actions are tested and leads to fail node)
 *      =>  convertSelectedNodeToFail
 *   actionInSelected is empty & not AllChildrenInSelectedAreFail  (all actions are tested but some is not fail)
 *      => actionInSelected = nodeSelector.selectChild(), stepReturn=applyAction(actionInSelected), backPropagate(stepReturn)
 * <p>
 *  The parameter actionTemplate is needed as a "seed" to create a tree with unknown types.
 */


@Setter
@Getter
@Log
public class MonteCarloTreeCreator<S, A> {
    private static final double VALUE_MEMORY_IF_NOT_TERMINAL = 0d;
    private static final String START_GIVE_FAIL_MESSAGE = "All actions in start start give fail";
    private static final String NO_FIRST_ACTION_MESSAGE = "No first action present - probably to small time budget";
    EnvironmentGenericInterface<S, A> environment;
    StateInterface<S> startState;
    MonteCarloSettings<S, A> settings;
    ActionInterface<A> actionTemplate;
    MemoryInterface<S> memory;

    NodeWithChildrenInterface<S, A> nodeRoot;
    TreeInfoHelper<S, A> tih;
    CpuTimer cpuTimer;
    int nofIterations;
    List<ActionInterface<A>> actionsToSelected;
    List<TreePlotData> plotData;

    @Builder
    private static <S, A> MonteCarloTreeCreator<S, A> newMCTC(
            @NonNull EnvironmentGenericInterface<S, A> environment,
            @NonNull StateInterface<S> startState,
            @NonNull MonteCarloSettings<S, A> monteCarloSettings,
            @NonNull ActionInterface<A> actionTemplate,
            MemoryInterface<S> memory) {
        MonteCarloTreeCreator<S, A> mctc = new MonteCarloTreeCreator<>();
        mctc.environment = environment;
        mctc.startState = startState;
        mctc.settings = monteCarloSettings;

        mctc.actionTemplate = actionTemplate;
        Conditionals.executeOneOfTwo(Objects.isNull(memory),
                () -> mctc.memory = NodeValueMemoryHashMap.newEmpty(),
                () -> mctc.memory = memory);

        MonteCarloTreeCreatorHelper.setSomeFields(startState, mctc);
        return mctc;
    }

    public MonteCarloSearchStatistics<S, A> getStatistics() {
        MonteCarloSearchStatistics<S, A> statistics = new MonteCarloSearchStatistics<>(nodeRoot, this, settings);
        statistics.setStatistics();
        return statistics;
    }

    public ActionInterface<A> getFirstAction() {
        TreeInfoHelper<S, A> tih = new TreeInfoHelper<>(nodeRoot, settings);
        ActionInterface<A> actionRoot = actionTemplate.copy();
        Conditionals.executeIfTrue(tih.getValueOfFirstBestAction().isEmpty(), () ->
                log.warning(NO_FIRST_ACTION_MESSAGE));
        A actionValue = tih.getValueOfFirstBestAction().orElse(actionTemplate.getValue());
        actionRoot.setValue(actionValue);
        return actionRoot;
    }

    public NodeWithChildrenInterface<S, A> run() throws StartStateIsTrapException {
        MonteCarloTreeCreatorHelper.setSomeFields(startState, this);  //needed because setStartState will not affect correctly otherwise
        MonteCarloTreeCreatorHelper<S, A> helper=new MonteCarloTreeCreatorHelper<>(
                environment,settings,actionTemplate,startState,nodeRoot,cpuTimer);
        if (helper.startStateIsTrap()) {
            throw new StartStateIsTrapException(START_GIVE_FAIL_MESSAGE);
        }

        int i;
        plotData.clear();
        ActionSelector<S, A> actionSelector = new ActionSelector<>(settings, actionTemplate);
        for (i = 0; i < settings.maxNofIterations; i++) {
            NodeWithChildrenInterface<S, A> nodeSelected = select(nodeRoot);
            Optional<ActionInterface<A>> actionInSelected = actionSelector.selectRandomNonTestedAction(nodeSelected);
            helper.someLogging(i, nodeSelected, actionInSelected);
            if (actionInSelected.isPresent()) {
                StepReturnGeneric<S> sr = applyActionAndExpand(nodeSelected, actionInSelected.get());
                SimulationResults simulationResults = simulate(sr.newState, nodeSelected.getDepth());
                backPropagate(sr, simulationResults, actionInSelected.get());
            } else {  // actionInSelected is empty <=> all actions tested
                chooseTestedActionAndBackPropagate(nodeSelected, actionSelector);
            }

            helper.updatePlotData(plotData);
            if (cpuTimer.isTimeExceeded()) {
                log.fine("Time exceeded");
                break;
            }
        }
        nofIterations = i;
        helper.logStatistics(i, new MonteCarloSearchStatistics<>(nodeRoot, this, settings));
        return nodeRoot;
    }


    private NodeWithChildrenInterface<S, A> select(NodeWithChildrenInterface<S, A> nodeRoot) {
        NodeSelector<S, A> ns = new NodeSelector<>(nodeRoot, settings, settings.coefficientExploitationExploration);
        NodeWithChildrenInterface<S, A> nodeSelected = ns.select();
        actionsToSelected = ns.getActionsFromRootToSelected();
        return nodeSelected;
    }

    private StepReturnGeneric<S> applyActionAndExpand(NodeWithChildrenInterface<S, A> nodeSelected,
                                                      ActionInterface<A> actionInSelected) {
        StateInterface<S> state = TreeInfoHelper.getState(startState, environment, actionsToSelected);
        StepReturnGeneric<S> sr = environment.step(actionInSelected, state);
        nodeSelected.saveRewardForAction(actionInSelected, sr.reward);
        NodeInterface<S, A> child = NodeInterface.newNode(sr, actionInSelected);
        child.setDepth(nodeSelected.getDepth() + 1);  //easy to forget
        boolean isChildAddedEarlier = NodeInfoHelper.findNodeMatchingNode(nodeSelected.getChildNodes(), child).isPresent();
        boolean isSelectedNotTerminal = nodeSelected.isNotTerminal();
        boolean isChildToDeep = child.getDepth() > settings.maxTreeDepth;
        maybeLogg(nodeSelected, child, isChildAddedEarlier, isChildToDeep);
        Conditionals.executeIfTrue(isSelectedNotTerminal &&
                isChildOkToAdd(isChildAddedEarlier, isChildToDeep), () ->
                nodeSelected.addChildNode(child));
        return sr;
    }

    private void maybeLogg(NodeWithChildrenInterface<S, A> nodeSelected,
                           NodeInterface<S, A> child,
                           boolean isChildAddedEarlier,
                           boolean isChildToDeep) {
        Conditionals.executeIfTrue(!isChildOkToAdd(isChildAddedEarlier, isChildToDeep), () ->
                log.fine("Child will not be added, child = " + child.getName() + ", in node = " + nodeSelected.getName()
                        + ", isChildAddedEarlier =" + isChildAddedEarlier + ", isChildToDeep =" + isChildToDeep));
    }

    private boolean isChildOkToAdd(boolean isChildAddedEarlier, boolean isChildToDeep) {
        return !isChildAddedEarlier && !isChildToDeep;
    }

    public SimulationResults simulate(StateInterface<S> stateAfterApplyingActionInSelectedNode) {
        return simulate(stateAfterApplyingActionInSelectedNode, 0);
    }

    public SimulationResults simulate(StateInterface<S> stateAfterApplyingActionInSelectedNode,
                                      int startDepth) {
        SimulationResults simulationResults = SimulationResults.newEmpty();
        for (int i = 0; i < settings.nofSimulationsPerNode; i++) {
            List<StepReturnGeneric<S>> stepResults =
                    stepToTerminal(stateAfterApplyingActionInSelectedNode.copy(), startDepth);
            StepReturnGeneric<S> endReturn = stepResults.get(stepResults.size() - 1);
            double sumOfRewards = ListUtils.discountedSum(
                    stepResults.stream().map(r -> r.reward).collect(Collectors.toList()),
                    settings.discountFactorSimulation);
            boolean isEndingInFail = endReturn.isFail;
            simulationResults.add(sumOfRewards, isEndingInFail);
        }
        return simulationResults;
    }

    private List<StepReturnGeneric<S>> stepToTerminal(StateInterface<S> state,
                                                      int startDepth) {
        List<StepReturnGeneric<S>> returns = new ArrayList<>();
        SimulationPolicyInterface<S, A> policy = settings.simulationPolicy;
        StepReturnGeneric<S> stepReturn;
        int depth = startDepth;
        do {
            ActionInterface<A> action = policy.chooseAction(state);
            stepReturn = environment.step(action, state);
            state.setFromReturn(stepReturn);
            returns.add(stepReturn);
            depth++;
        } while (!stepReturn.isTerminal && depth < settings.maxSimulationDepth);
        return returns;
    }

    private void chooseTestedActionAndBackPropagate(NodeWithChildrenInterface<S, A> nodeSelected, ActionSelector<S, A> actionSelector) {
        log.fine("The selected node has either all children as fails or is at max depth");
        StateInterface<S> state = TreeInfoHelper.getState(startState, environment, actionsToSelected);
        Optional<ActionInterface<A>> actionInSelected = actionSelector.selectBestTestedAction(nodeSelected);
        StepReturnGeneric<S> sr = environment.step(actionInSelected.orElseThrow(), state);
        SimulationResults simulationResults = SimulationResults.newEmpty();
        backPropagate(sr, simulationResults, actionInSelected.orElseThrow());
    }

    private void backPropagate(StepReturnGeneric<S> sr,
                               SimulationResults simulationResults,
                               ActionInterface<A> actionInSelected) {
        SimulationReturnsExtractor<S, A> bumSim = SimulationReturnsExtractor.<S, A>builder()
                .nofNodesOnPath(actionsToSelected.size() + 1)
                .simulationResults(simulationResults)
                .settings(settings)
                .build();


        BackupModifier<S, A> bum = BackupModifier.<S, A>builder().rootTree(nodeRoot)
                .actionsToSelected(actionsToSelected)
                .actionOnSelected(actionInSelected)
                .stepReturnOfSelected(sr)
                .settings(settings)
                .returnsSimulation(bumSim.getSimulationReturns())
                .memoryValueStateAfterAction(memory.read(sr.newState))
                .build();
        bum.backup();
    }


}
