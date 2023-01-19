package monte_carlo_tree_search.domains.battery_cell;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.generic_interfaces.SimulationPolicyInterface;
import monte_carlo_tree_search.generic_interfaces.StateInterface;

import java.util.Collections;
import java.util.List;

public class BestFeasiblePolicy implements SimulationPolicyInterface<CellVariables, Integer> {

    ActionInterface<Integer> actionTemplate;
    CellPolicyHelper cellPolicyHelper;

    public BestFeasiblePolicy(ActionInterface<Integer> actionTemplate,
                                EnvironmentGenericInterface<CellVariables, Integer> environment) {
        this.actionTemplate = actionTemplate;
        this.cellPolicyHelper=new CellPolicyHelper(environment);
    }

    @Override
    public ActionInterface<Integer> chooseAction(StateInterface<CellVariables> state) {
        ActionInterface<Integer> action=actionTemplate.copy();

        List<Integer> feasibleValueList = cellPolicyHelper.getValueList(state, action);

        int av=(feasibleValueList.isEmpty())
                ? action.nonApplicableAction()
                : Collections.max(feasibleValueList);
        action.setValue(av);
        return action;
    }

}
