package mcts_spacegame.enums;

import java.util.*;

/**
 * notApplicable: for root node
 */

public enum ShipActionREMOVE {
        up,still,down, notApplicable;

        /*
        public static List<ShipActionREMOVE> applicableActions () {
                return Arrays.asList(up,still,down);
        }

        public static List<ShipActionREMOVE> getAllActions(List<ShipActionREMOVE> actionsToSelected, ShipActionREMOVE actionOnSelected) {
                List<ShipActionREMOVE> actionOnSelectedList = Collections.singletonList(actionOnSelected);
                List<ShipActionREMOVE> actions = new ArrayList<>();
                actions.addAll(actionsToSelected);
                actions.addAll(actionOnSelectedList);
                return actions;
        }
 */
}
