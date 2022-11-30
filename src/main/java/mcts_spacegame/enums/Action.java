package mcts_spacegame.enums;

import java.lang.reflect.Array;
import java.util.*;

/**
 * notApplicable for root node
 */

public enum Action {
        up,still,down, notApplicable;

        public static List<Action> applicableActions () {
                return Arrays.asList(up,still,down);
        }

        public static List<Action> getAllActions(List<Action> actionsToSelected, Action actionOnSelected) {
                List<Action> actionOnSelectedList = Collections.singletonList(actionOnSelected);
                List<Action> actions = new ArrayList<>();
                actions.addAll(actionsToSelected);
                actions.addAll(actionOnSelectedList);
                return actions;
        }

}
