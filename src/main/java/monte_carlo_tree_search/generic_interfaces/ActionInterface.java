package monte_carlo_tree_search.generic_interfaces;
import monte_carlo_tree_search.domains.models_space.ActionShip;
import monte_carlo_tree_search.domains.models_space.ShipActionSet;

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
    Set<AV> applicableActions();
    AV nonApplicableAction();

     static <AV> List<AV> mergeActionsWithAction(List<AV> actionsToSelected, AV actionOnSelected) {
        List<AV> actionOnSelectedList = Collections.singletonList(actionOnSelected);
        List<AV> actions = new ArrayList<>();
        actions.addAll(actionsToSelected);
        actions.addAll(actionOnSelectedList);
        return actions;
    }

    static <AV>  List<AV> getNonTestedActionValues(List<AV> testedValues,Set<AV> allValues) {
        List<AV> nonTestedValues=new ArrayList<>(allValues);
        nonTestedValues.removeAll(testedValues);
        return nonTestedValues;
    }

    public static <AV> ActionInterface<AV> newAction(AV value) {
        if (value instanceof ShipActionSet) {
            ActionInterface<AV> action= (ActionInterface<AV>) new ActionShip(ShipActionSet.still);  //Todo not clean
            action.setValue(value);
            return action;
        } else {
            throw new IllegalArgumentException("Not known type");
        }
    }



}
