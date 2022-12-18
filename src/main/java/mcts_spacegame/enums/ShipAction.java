package mcts_spacegame.enums;

import java.util.*;

/**
 * notApplicable: for root node
 */

public enum ShipAction {
        up,still,down, notApplicable;

        public static List<ShipAction> applicableActions () {
                return Arrays.asList(up,still,down);
        }

        public static List<ShipAction> getAllActions(List<ShipAction> actionsToSelected, ShipAction actionOnSelected) {
                List<ShipAction> actionOnSelectedList = Collections.singletonList(actionOnSelected);
                List<ShipAction> actions = new ArrayList<>();
                actions.addAll(actionsToSelected);
                actions.addAll(actionOnSelectedList);
                return actions;
        }

}
