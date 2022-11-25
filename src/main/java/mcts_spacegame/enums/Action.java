package mcts_spacegame.enums;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * notApplicable for root node
 */

public enum Action {
        up,still,down, notApplicable;

        public static List<Action> applicableActions () {
                return Arrays.asList(up,still,down);
        }

}
