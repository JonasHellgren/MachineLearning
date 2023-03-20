package monte_carlo_tree_search.create_tree;

import common.Conditionals;
import common.CpuTimer;
import lombok.NonNull;
import lombok.extern.java.Log;
import monte_carlo_tree_search.models_and_support_classes.StepReturnGeneric;
import monte_carlo_tree_search.models_and_support_classes.TreePlotData;
import monte_carlo_tree_search.interfaces.ActionInterface;
import monte_carlo_tree_search.interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.interfaces.StateInterface;
import monte_carlo_tree_search.helpers.NodeInfoHelper;
import monte_carlo_tree_search.helpers.TreeInfoHelper;
import monte_carlo_tree_search.node_models.NodeInterface;
import monte_carlo_tree_search.node_models.NodeWithChildrenInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This MonteCarloTreeCreator helper class includes methods that are less critical/includes no major logic
 */

@Log
public class MonteCarloTreeCreatorHelper<S, A> {

    EnvironmentGenericInterface<S, A> environment;
    MonteCarloSettings<S, A> settings;
    ActionInterface<A> actionTemplate;
    StateInterface<S> startState;
    NodeWithChildrenInterface<S, A> nodeRoot;
    CpuTimer cpuTimer;

    public MonteCarloTreeCreatorHelper(EnvironmentGenericInterface<S, A> environment,
                                       MonteCarloSettings<S, A> settings,
                                       ActionInterface<A> actionTemplate,
                                       StateInterface<S> startState,
                                       NodeWithChildrenInterface<S, A> nodeRoot,
                                       CpuTimer cpuTimer) {
        this.environment = environment;
        this.settings = settings;
        this.actionTemplate = actionTemplate;
        this.startState = startState;
        this.nodeRoot=nodeRoot;
        this.cpuTimer=cpuTimer;
    }

    static <S, A> void setSomeFields(@NonNull StateInterface<S> startState, MonteCarloTreeCreator<S, A> mctc) {
        ActionInterface<A> actionRoot = mctc.actionTemplate.copy();
        actionRoot.setValue(actionRoot.nonApplicableAction());
        mctc.nodeRoot = NodeInterface.newNotTerminal(startState, actionRoot);
        mctc.tih = new TreeInfoHelper<>(mctc.nodeRoot, mctc.settings);
        mctc.nofIterations = 0;
        mctc.plotData = new ArrayList<>();
    }

    //https://logfetch.com/java-list-check-all-values-true/
     boolean startStateIsTrap() {
        List<Boolean> failList=new ArrayList<>();
        for (A actionValue: settings.actionSelectionPolicy.availableActionValues(startState)) {
            ActionInterface<A> action = actionTemplate.copy();
            action.setValue(actionValue);
            StepReturnGeneric<S> sr = environment.step(action, startState);
            failList.add(sr.isFail);
        }
        return failList.stream().allMatch(f -> f==true);
    }

    void someLogging(int i,
                     NodeWithChildrenInterface<S, A> nodeSelected,
                     Optional<ActionInterface<A>> actionInSelected) {  //generally avoid optional as parameter but exception here, just logging
        log.fine("i = " + i);
        log.fine("nodeSelected = " + nodeSelected + ", nof child nodes = " + nodeSelected.getChildNodes().size());
        log.fine("actionInSelected = " + actionInSelected);
        log.fine("nodeRoot action values = " + NodeInfoHelper.actionValuesNode(actionTemplate, nodeRoot));
    }

    void updatePlotData(List<TreePlotData> plotData) {
        TreeInfoHelper<S, A> tih = new TreeInfoHelper<>(nodeRoot, settings);
        if (settings.isCreatePlotData) {
            TreePlotData data = TreePlotData.builder()
                    .maxValue(NodeInfoHelper.valueNode(actionTemplate, nodeRoot)).nofNodes(tih.nofNodes())
                    .maxDepth(tih.maxDepth()).build();
            plotData.add(data);
        }
    }

    void logStatistics(int nofIterations,MonteCarloSearchStatistics<S, A> statistics) {
        this.cpuTimer.stop();
        TreeInfoHelper<S, A> tih = new TreeInfoHelper<>(nodeRoot, settings);

        log.info("time used = " + cpuTimer.getAbsoluteProgress() + ", nofIterations = " + nofIterations +
                ", max tree depth = " + tih.maxDepth() + ", depth of best path = " + tih.getActionsOnBestPath().size()
                + ", nof nodes = " + statistics.nofNodes + ", branching = " + statistics.averageNofChildrenPerNode);
    }

    static <S, A> void  maybeLogg(NodeWithChildrenInterface<S, A> nodeSelected,
                           NodeInterface<S, A> child,
                           boolean isChildAddedEarlier,
                           boolean isChildToDeep) {
        Conditionals.executeIfTrue(!isChildOkToAdd(isChildAddedEarlier, isChildToDeep), () ->
                log.fine("Child will not be added, child = " + child.getName() + ", in node = " + nodeSelected.getName()
                        + ", isChildAddedEarlier =" + isChildAddedEarlier + ", isChildToDeep =" + isChildToDeep));
    }

    static boolean isChildOkToAdd(boolean isChildAddedEarlier, boolean isChildToDeep) {
        return !isChildAddedEarlier && !isChildToDeep;
    }

}
