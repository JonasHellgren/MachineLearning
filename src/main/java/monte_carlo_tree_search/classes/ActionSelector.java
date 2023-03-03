package monte_carlo_tree_search.classes;

import common.RandUtils;
import lombok.extern.java.Log;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.node_models.NodeInterface;
import monte_carlo_tree_search.node_models.NodeWithChildrenInterface;
import org.jetbrains.annotations.NotNull;

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

    public Optional<ActionInterface<A>> selectRandomNonTestedAction(NodeWithChildrenInterface<S,A> nodeSelected) {
        int nofTestedActions=getTestedActions(nodeSelected).size();

        List<ActionInterface<A>> nonTestedActions = (nofTestedActions==0)
                ? Collections.singletonList(getActionFromPolicy(nodeSelected))
                : getNonTestedActionsInPolicy(nodeSelected);

        if(nonTestedActions.size()==0) {
            return Optional.empty();
        } else {
            return Optional.of(getRandomAction(nonTestedActions));
        }
    }


    public Optional<ActionInterface<A>> selectRandomTestedAction(NodeWithChildrenInterface<S,A> nodeSelected) {
        int nofTestedActions=getTestedActions(nodeSelected).size();

        if(nofTestedActions==0) {
            return Optional.empty();
        } else {
            return Optional.of(getRandomAction(getTestedActions(nodeSelected)));
        }
    }

    private ActionInterface<A> getActionFromPolicy(NodeInterface<S,A> nodeSelected) {
        return settings.firstActionSelectionPolicy.chooseAction(nodeSelected.getState());
    }

    private ActionInterface<A> getRandomAction(List<ActionInterface<A>> actions) {
        return randUtils.getRandomItemFromList(actions);
    }

    private List<ActionInterface<A>> getNonTestedActions(NodeWithChildrenInterface<S,A> nodeSelected) {
        List<ActionInterface<A>> testedActions = getTestedActions(nodeSelected);
        List<A> testedActionValues=testedActions.stream().map(ActionInterface::getValue).collect(Collectors.toList());
        Set<A> allValues=nodeSelected.getAction().applicableActions();
        List<A> nonTestedActionValues=ActionInterface.getNonTestedActionValues(testedActionValues,allValues);
        return getActionsFromValues(nonTestedActionValues);
    }



    private List<ActionInterface<A>> getNonTestedActionsInPolicy(NodeWithChildrenInterface<S,A> nodeSelected) {
        List<ActionInterface<A>> testedActions = getTestedActions(nodeSelected);
        List<A> testedActionValues=testedActions.stream().map(ActionInterface::getValue).collect(Collectors.toList());
        Set<A> allValues=settings.firstActionSelectionPolicy.availableActionValues(nodeSelected.getState());
        Set<A> allValuesSet=new HashSet<>(allValues);
        List<A> nonTestedActionValues=ActionInterface.getNonTestedActionValues(testedActionValues,allValuesSet);

     /*   System.out.println("allValuesSet = " + allValuesSet);
        System.out.println("testedActionValues = " + testedActionValues);
        System.out.println("nonTestedActionValues = " + nonTestedActionValues);  */

        return getActionsFromValues(nonTestedActionValues);
    }

    @NotNull
    private List<ActionInterface<A>> getActionsFromValues(List<A> nonTestedActionValues) {
        List<ActionInterface<A>> nonTestedActions = new ArrayList<>();
        for (A value : nonTestedActionValues) {
            ActionInterface<A> action = actionTemplate.copy();
            action.setValue(value);
            nonTestedActions.add(action);
        }
        return nonTestedActions;
    }

    private List<ActionInterface<A>> getTestedActions(NodeWithChildrenInterface<S,A> nodeSelected) {
        return nodeSelected.getChildNodes().stream()
                .map(NodeInterface::getAction).collect(Collectors.toList());
    }

}
