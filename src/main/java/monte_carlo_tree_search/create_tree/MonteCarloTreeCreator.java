package monte_carlo_tree_search.create_tree;

import common.other.Conditionals;
import common.other.CpuTimer;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.java.Log;
import monte_carlo_tree_search.models_and_support_classes.*;
import monte_carlo_tree_search.exceptions.StartStateIsTrapException;
import monte_carlo_tree_search.interfaces.*;
import monte_carlo_tree_search.helpers.NodeInfoHelper;
import monte_carlo_tree_search.helpers.TreeInfoHelper;
import monte_carlo_tree_search.search_tree_node_models.NodeInterface;
import monte_carlo_tree_search.search_tree_node_models.NodeWithChildrenInterface;

import java.util.*;

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
    private static final String NO_STATISTICS_MESSAGE = "No statistics set, need to first call run()";
    private static final String NO_BEST_FIRST_ACTION_MESSAGE = "No best first action present, selecting random action";
    EnvironmentGenericInterface<S, A> environment;
    StateInterface<S> startState;
    MonteCarloSettings<S, A> settings;
    ActionInterface<A> actionTemplate;
    ReadableMemoryInterface<S> memory;

    NodeWithChildrenInterface<S, A> nodeRoot;
    TreeInfoHelper<S, A> tih;

    int nofIterations;
    List<ActionInterface<A>> actionsToSelected;
    List<TreePlotData> plotData;
    MonteCarloSearchStatistics<S, A> statistics;

    @Builder
    private static <S, A> MonteCarloTreeCreator<S, A> newMCTC(
            @NonNull EnvironmentGenericInterface<S, A> environment,
            @NonNull StateInterface<S> startState,
            @NonNull MonteCarloSettings<S, A> monteCarloSettings,
            @NonNull ActionInterface<A> actionTemplate,
            ReadableMemoryInterface<S> memory) {
        MonteCarloTreeCreator<S, A> mctc = new MonteCarloTreeCreator<>();
        mctc.environment = environment;
        mctc.startState = startState;
        mctc.settings = monteCarloSettings;

        mctc.actionTemplate = actionTemplate;
        Conditionals.executeOneOfTwo(Objects.isNull(memory),
                () -> mctc.memory = ValueMemoryHashMap.newEmpty(),
                () -> mctc.memory = memory);

        MonteCarloTreeCreatorHelper.setSomeFields(startState, mctc);
        return mctc;
    }

    public MonteCarloSearchStatistics<S, A> getStatistics() {
        if (statistics == null) {
            throw new RuntimeException(NO_STATISTICS_MESSAGE);
        }
        statistics.setStatistics();
        return statistics;
    }

    public ActionInterface<A> getFirstAction() {
        NodeSelector<S, A> ns = new NodeSelector<>(nodeRoot, settings);
        Optional<NodeInterface<S, A>> bestChild = ns.selectBestNonFailChild(nodeRoot);

        if (bestChild.isEmpty()) {
            log.warning(NO_BEST_FIRST_ACTION_MESSAGE);
            ActionSelector<S, A> actionSelector = new ActionSelector<>(settings, actionTemplate);
            return actionSelector.getRandomAction();
        }
        return bestChild.orElseThrow().getAction();
    }

    public NodeWithChildrenInterface<S, A> run() throws StartStateIsTrapException {
        CpuTimer cpuTimer = CpuTimer.newWithTimeBudgetInMilliSec(settings.timeBudgetMilliSeconds);
        MonteCarloTreeCreatorHelper.setSomeFields(startState, this);  //needed because setStartState will not affect correctly otherwise
        MonteCarloTreeCreatorHelper<S, A> helper = new MonteCarloTreeCreatorHelper<>(
                environment, settings, actionTemplate, startState, nodeRoot, cpuTimer);
        if (helper.startStateIsTrap()) {
            throw new StartStateIsTrapException(START_GIVE_FAIL_MESSAGE);
        }

        MonteCarloSimulator<S, A> simulator = new MonteCarloSimulator<>(environment, settings);
        Counter counter = new Counter(settings.minNofIterations, settings.maxNofIterations);
        plotData.clear();
        ActionSelector<S, A> actionSelector = new ActionSelector<>(settings, actionTemplate);

        while (isCounterAndTimeValid(counter, cpuTimer)) {
            NodeWithChildrenInterface<S, A> nodeSelected = select(nodeRoot);
            Optional<ActionInterface<A>> actionInSelected = actionSelector.selectRandomNonTestedAction(nodeSelected);
            helper.someLogging(counter.getCount(), nodeSelected, actionInSelected);
            BackPropagator<S, A> backPropagator = createBackPropagator();
            if (actionInSelected.isPresent()) {
                StepReturnGeneric<S> sr = applyActionAndExpand(nodeSelected, actionInSelected.get());
                SimulationResults simulationResults = simulator.simulate(sr.newState, sr.isTerminal, nodeSelected.getDepth());
                backPropagator.backPropagate(sr, simulationResults, actionInSelected.get());
            } else {  // actionInSelected is empty <=> all actions tested
                backPropagator.chooseTestedActionAndBackPropagate(nodeSelected, actionSelector);
            }

            log.fine("valueMap nodeRoot = " + NodeInfoHelper.actionValueMap(actionTemplate, nodeRoot));
            helper.updatePlotData(plotData);
            counter.increase();
        }
        nofIterations = counter.getCount();
        statistics = new MonteCarloSearchStatistics<>(nodeRoot, this, cpuTimer, settings);
        Conditionals.executeIfTrue(settings.isDoLogging, () ->
                helper.logStatistics(counter.getCount(), statistics));
        return nodeRoot;
    }

    private boolean isCounterAndTimeValid(Counter counter, CpuTimer cpuTimer) {
        if (counter.isBelowMinCount()) {
            return true;
        }
        boolean notValid = counter.isExceeded() || cpuTimer.isTimeExceeded();
        return !notValid;
    }

    private BackPropagator<S, A> createBackPropagator() {
        return BackPropagator.<S, A>builder()
                .environment(environment).startState(startState).settings(settings).nodeRoot(nodeRoot)
                .actionsToSelected(actionsToSelected).memory(memory)
                .build();
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
        MonteCarloTreeCreatorHelper.maybeLogg(nodeSelected, child, isChildAddedEarlier, isChildToDeep);
        Conditionals.executeIfTrue(isSelectedNotTerminal &&
                MonteCarloTreeCreatorHelper.isChildOkToAdd(isChildAddedEarlier, isChildToDeep), () ->
                nodeSelected.addChildNode(child));
        return sr;
    }


}
