package monte_carlo_tree_search.interfaces;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * AV is generic type for action variables
 */

public interface ActionInterface<A> {
    void setValue(A actionValue);
    A getValue();
    ActionInterface<A> copy();
    Set<A> applicableActions();
    A nonApplicableAction();

     static <A> List<A> mergeActionsWithAction(List<A> actionsToSelected, A actionOnSelected) {
        List<A> actionOnSelectedList = Collections.singletonList(actionOnSelected);
        List<A> actions = new ArrayList<>();
        actions.addAll(actionsToSelected);
        actions.addAll(actionOnSelectedList);
        return actions;
    }

    static <A>  List<A> getNonTestedActionValues(List<A> testedValues, Set<A> allValues) {
        List<A> nonTestedValues=new ArrayList<>(allValues);
        nonTestedValues.removeAll(testedValues);
        return nonTestedValues;
    }

}
