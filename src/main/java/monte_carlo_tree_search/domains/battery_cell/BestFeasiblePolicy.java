package monte_carlo_tree_search.domains.battery_cell;
import monte_carlo_tree_search.interfaces.ActionInterface;
import monte_carlo_tree_search.interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.interfaces.SimulationPolicyInterface;
import monte_carlo_tree_search.interfaces.StateInterface;

import java.util.Collections;
import java.util.List;
import java.util.Set;

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

    @Override
    public Set<Integer> availableActionValues(StateInterface<CellVariables> state) {
        return actionTemplate.applicableActions();
    }

}
