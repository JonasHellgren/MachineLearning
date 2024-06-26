package monte_carlo_tree_search.domains.battery_cell;

import monte_carlo_tree_search.models_and_support_classes.StepReturnGeneric;
import monte_carlo_tree_search.interfaces.ActionInterface;
import monte_carlo_tree_search.interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.interfaces.StateInterface;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CellPolicyHelper {

    EnvironmentGenericInterface<CellVariables, Integer> environment;

    public CellPolicyHelper(EnvironmentGenericInterface<CellVariables, Integer> environment) {
        this.environment = environment;
    }

    @NotNull
    List<Integer> getValueList(StateInterface<CellVariables> state, ActionInterface<Integer> action) {
        List<Integer> feasibleValueList=new ArrayList<>();
        for (Integer av: action.applicableActions())  {
            action.setValue(av);
            StepReturnGeneric<CellVariables> sr=environment.step(action, state);
            if (!sr.isFail) {
                feasibleValueList.add(av);
            }
        }
        return feasibleValueList;
    }
}
