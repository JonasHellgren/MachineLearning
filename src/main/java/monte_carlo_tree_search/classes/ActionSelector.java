package monte_carlo_tree_search.classes;

import common.RandUtils;
import lombok.extern.java.Log;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.node_models.NodeInterface;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is for determining the action to choose in the selected node, the action that will lead to tree
 * expansion.
 */

@Log
public class ActionSelector<S,A> {
    MonteCarloSettings<S,A> settings;
    RandUtils<ActionInterface<A>> randUtils;
    ActionInterface<A> actionTemplate;

    public ActionSelector(MonteCarloSettings<S,A> settings, ActionInterface<A> actionTemplate) {
        this.randUtils=new RandUtils<>();
        this.settings=settings;
        this.actionTemplate=actionTemplate;
    }

    public Optional<ActionInterface<A>> select(NodeInterface<S,A> nodeSelected) {
        int nofTestedActions=getTestedActions(nodeSelected).size();

        List<ActionInterface<A>> nonTestedActions = (nofTestedActions==0)
                ? Collections.singletonList(getActionFromPolicy(nodeSelected))
                : getNonTestedActions(nodeSelected);

        if(nonTestedActions.size()==0) {
            return Optional.empty();
        } else {
            return Optional.of(getRandomAction(nonTestedActions));
        }
    }

    private ActionInterface<A> getActionFromPolicy(NodeInterface<S,A> nodeSelected) {
        return settings.firstActionSelectionPolicy.chooseAction(nodeSelected.getState());
    }

    private ActionInterface<A> getRandomAction(List<ActionInterface<A>> actions) {
        return randUtils.getRandomItemFromList(actions);
    }

    private List<ActionInterface<A>> getNonTestedActions(NodeInterface<S,A> nodeSelected) {
        List<ActionInterface<A>> testedActions = getTestedActions(nodeSelected);
        List<A> testedActionValues=testedActions.stream().map(ActionInterface::getValue).collect(Collectors.toList());
        Set<A> allValues=nodeSelected.getAction().applicableActions();
        List<A> nonTestedActionValues=ActionInterface.getNonTestedActionValues(testedActionValues,allValues);
        List<ActionInterface<A>> nonTestedActions=new ArrayList<>();

        for (A value:nonTestedActionValues) {
            ActionInterface<A> action=actionTemplate.copy();
            action.setValue(value);
            nonTestedActions.add(action);
        }

        return nonTestedActions;
    }

    private List<ActionInterface<A>> getTestedActions(NodeInterface<S,A> nodeSelected) {
        return nodeSelected.getChildNodes().stream()
                .map(NodeInterface::getAction).collect(Collectors.toList());
    }

}
