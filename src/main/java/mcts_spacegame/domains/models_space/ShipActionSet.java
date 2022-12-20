package mcts_spacegame.domains.models_space;

import java.util.*;

public enum ShipActionSet {
    up,still,down, notApplicable;

    public static Set<ShipActionSet> applicableActions() {
        return new HashSet<>(Arrays.asList(ShipActionSet.up, ShipActionSet.still, ShipActionSet.down));
    }

    public static ShipActionSet nonApplicableAction() {
        return ShipActionSet.notApplicable;
    }

    public static List<ShipActionSet> getNonTestedActionValues(List<ShipActionSet> values) {
        List<ShipActionSet> nonTestedValues=new ArrayList<>(applicableActions());
        nonTestedValues.removeAll(values);
        return nonTestedValues;
    }

}
