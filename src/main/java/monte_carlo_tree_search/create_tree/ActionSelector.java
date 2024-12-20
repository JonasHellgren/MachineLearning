package monte_carlo_tree_search.create_tree;

import common.other.RandUtilsML;
import lombok.extern.java.Log;
import monte_carlo_tree_search.interfaces.ActionInterface;
import monte_carlo_tree_search.search_tree_node_models.NodeInterface;
import monte_carlo_tree_search.search_tree_node_models.NodeWithChildrenInterface;
import org.apache.commons.math3.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is for determining the action to choose in the selected node, the action that will lead to tree
 * expansion.
 * <p>
 * The most critical method is selectRandomNonTestedAction. The selected action is determined by the action
 * selection policy. This policy can, for a specific stateNew, provide an action set smaller than the set according
 * to the environment of the domain. The elevator domain is a clear example, if moving between two floors, only
 * one action is available, the present speed. This "trick" can lead no enormous search tree branching factor decrease.
 */

@Log
public class ActionSelector<S, A> {
    MonteCarloSettings<S, A> settings;
    RandUtilsML<ActionInterface<A>> randUtils;
    ActionInterface<A> actionTemplate;

    public ActionSelector(MonteCarloSettings<S, A> settings, ActionInterface<A> actionTemplate) {
        this.randUtils = new RandUtilsML<>();
        this.settings = settings;
        this.actionTemplate = actionTemplate;
    }

    public Optional<ActionInterface<A>> selectRandomNonTestedAction(NodeWithChildrenInterface<S, A> nodeSelected) {
        int nofTestedActions = getTestedActions(nodeSelected).size();
        List<ActionInterface<A>> nonTestedActions = (nofTestedActions == 0)
                ? Collections.singletonList(getActionFromPolicy(nodeSelected))
                : getNonTestedActionsInPolicy(nodeSelected);

        return (nonTestedActions.size() == 0)
                ? Optional.empty()
                : Optional.of(getRandom(nonTestedActions));
    }

    public Optional<ActionInterface<A>> selectBestTestedAction(NodeWithChildrenInterface<S, A> nodeSelected) {
        List<ActionInterface<A>> actions = getTestedActions(nodeSelected);

        if (actions.size() == 0) {
            log.warning("No actions tested");
            return Optional.empty();
        }

        List<Pair<ActionInterface<A>, Double>> pairs = new ArrayList<>();
        for (ActionInterface<A> action : actions) {
            pairs.add(new Pair<>(action, nodeSelected.getActionValue(action)));
        }
        Optional<Pair<ActionInterface<A>, Double>> bestPair = getPairWithHighestValue(pairs);

        return Optional.of(bestPair.orElseThrow().getFirst());
    }

    public ActionInterface<A> getRandomAction() {
        Set<A> actions = actionTemplate.applicableActions();
        RandUtilsML<A> randUtils = new RandUtilsML<>();
        actionTemplate.setValue(randUtils.getRandomItemFromList(new ArrayList<>(actions)));
        return actionTemplate;
    }

    public List<ActionInterface<A>> getNonTestedActionsInPolicy(NodeWithChildrenInterface<S, A> nodeSelected) {
        List<ActionInterface<A>> testedActions = getTestedActions(nodeSelected);
        List<A> testedActionValues = testedActions.stream().map(ActionInterface::getValue).collect(Collectors.toList());
        Set<A> allValues = settings.actionSelectionPolicy.availableActionValues(nodeSelected.getState());
        Set<A> allValuesSet = new HashSet<>(allValues);
        List<A> nonTestedActionValues = ActionInterface.getNonTestedActionValues(testedActionValues, allValuesSet);
        return getActionsFromValues(nonTestedActionValues);
    }

    private ActionInterface<A> getActionFromPolicy(NodeInterface<S, A> nodeSelected) {
        return settings.actionSelectionPolicy.chooseAction(nodeSelected.getState());
    }

    private Optional<Pair<ActionInterface<A>, Double>> getPairWithHighestValue(List<Pair<ActionInterface<A>, Double>> pairs) {
        return pairs.stream().
                reduce((res, item) -> res.getSecond() > item.getSecond() ? res : item);
    }


    private ActionInterface<A> getRandom(List<ActionInterface<A>> actions) {
        return randUtils.getRandomItemFromList(actions);
    }

    private List<ActionInterface<A>> getActionsFromValues(List<A> nonTestedActionValues) {
        List<ActionInterface<A>> nonTestedActions = new ArrayList<>();
        for (A value : nonTestedActionValues) {
            ActionInterface<A> action = actionTemplate.copy();
            action.setValue(value);
            nonTestedActions.add(action);
        }
        return nonTestedActions;
    }

    private List<ActionInterface<A>> getTestedActions(NodeWithChildrenInterface<S, A> nodeSelected) {
        return nodeSelected.getChildNodes().stream()
                .map(NodeInterface::getAction).collect(Collectors.toList());
    }

}
