package mcts_spacegame.generic_interfaces;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * AV is generic type for action variables
 */

public interface ActionInterface<AV> {
    void setValue(AV actionValue);
    AV getValue();
    Set<AV>  applicableActions();
    AV nonApplicableAction();

     static <AV> List<AV> mergeActionsWithAction(List<AV> actionsToSelected, AV actionOnSelected) {
        List<AV> actionOnSelectedList = Collections.singletonList(actionOnSelected);
        List<AV> actions = new ArrayList<>();
        actions.addAll(actionsToSelected);
        actions.addAll(actionOnSelectedList);
        return actions;
    }

}
