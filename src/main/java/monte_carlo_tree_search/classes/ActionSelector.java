package monte_carlo_tree_search.classes;

import common.RandUtils;
import lombok.extern.java.Log;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.node_models.NodeInterface;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is for determining the action to choose in the selected node, the action that will lead to tree
 * expansion.
 */

@Log
public class ActionSelector<SSV,AV> {
    MonteCarloSettings<SSV,AV> settings;
    RandUtils<ActionInterface<AV>> randUtils;
    ActionInterface<AV> actionTemplate;

    public ActionSelector(MonteCarloSettings<SSV,AV> settings, ActionInterface<AV> actionTemplate) {
        this.randUtils=new RandUtils<>();
        this.settings=settings;
        this.actionTemplate=actionTemplate;
    }

    public Optional<ActionInterface<AV>> select(NodeInterface<SSV,AV> nodeSelected) {
        int nofTestedActions=getTestedActions(nodeSelected).size();

        List<ActionInterface<AV>> nonTestedActions = (nofTestedActions==0)
                ? Collections.singletonList(getActionFromPolicy(nodeSelected))
                : getNonTestedActions(nodeSelected);

        if(nonTestedActions.size()==0) {
            return Optional.empty();
        } else {
            return Optional.of(getRandomAction(nonTestedActions));
        }
    }

    private ActionInterface<AV> getActionFromPolicy(NodeInterface<SSV,AV> nodeSelected) {
        return settings.firstActionSelectionPolicy.chooseAction(nodeSelected.getState());
    }

    private ActionInterface<AV> getRandomAction(List<ActionInterface<AV>> actions) {
        return randUtils.getRandomItemFromList(actions);
    }

    private List<ActionInterface<AV>> getNonTestedActions(NodeInterface<SSV,AV> nodeSelected) {
        List<ActionInterface<AV>> testedActions = getTestedActions(nodeSelected);
        List<AV> testedActionValues=testedActions.stream().map(ActionInterface::getValue).collect(Collectors.toList());
        Set<AV> allValues=nodeSelected.getAction().applicableActions();
        List<AV> nonTestedActionValues=ActionInterface.getNonTestedActionValues(testedActionValues,allValues);
        List<ActionInterface<AV>> nonTestedActions=new ArrayList<>();

        for (AV value:nonTestedActionValues) {
            ActionInterface<AV> action=actionTemplate.copy();
            action.setValue(value);
            nonTestedActions.add(action);
        }

        return nonTestedActions;
    }

    private List<ActionInterface<AV>> getTestedActions(NodeInterface<SSV,AV> nodeSelected) {
        return nodeSelected.getChildNodes().stream()
                .map(NodeInterface::getAction).collect(Collectors.toList());
    }

}
