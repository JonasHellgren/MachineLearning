package mcts_spacegame.models_battery_cell;

import mcts_spacegame.enums.ShipAction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public interface ActionInterface<AVT> {
    void setAction(AVT actionValue);
    AVT getAction();
    Set<AVT> applicableActions();
    AVT nonApplicableAction();

    default List<AVT> mergeActionsWithAction(List<AVT> actionsToSelected, AVT actionOnSelected) {
        List<AVT> actionOnSelectedList = Collections.singletonList(actionOnSelected);
        List<AVT> actions = new ArrayList<>();
        actions.addAll(actionsToSelected);
        actions.addAll(actionOnSelectedList);
        return actions;
    }

}
