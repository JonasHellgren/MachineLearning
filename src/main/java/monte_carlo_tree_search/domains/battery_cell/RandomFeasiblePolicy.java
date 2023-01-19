package monte_carlo_tree_search.domains.battery_cell;

import common.RandUtils;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.generic_interfaces.SimulationPolicyInterface;
import monte_carlo_tree_search.generic_interfaces.StateInterface;

import java.util.List;

public class RandomFeasiblePolicy  implements SimulationPolicyInterface<CellVariables, Integer> {

    ActionInterface<Integer> actionTemplate;
    CellPolicyHelper cellPolicyHelper;

    public RandomFeasiblePolicy(ActionInterface<Integer> actionTemplate,
                                EnvironmentGenericInterface<CellVariables, Integer> environment) {
        this.actionTemplate = actionTemplate;
        this.cellPolicyHelper=new CellPolicyHelper(environment);
    }

    @Override
    public ActionInterface<Integer> chooseAction(StateInterface<CellVariables> state) {
        ActionInterface<Integer> action=actionTemplate.copy();

        List<Integer> feasibleValueList = cellPolicyHelper.getValueList(state, action);

        RandUtils<Integer> randUtils=new RandUtils<>();
        int av=(feasibleValueList.isEmpty())
                ? action.nonApplicableAction()
                :randUtils.getRandomItemFromList(feasibleValueList);
        action.setValue(av);
        return action;
    }


}
