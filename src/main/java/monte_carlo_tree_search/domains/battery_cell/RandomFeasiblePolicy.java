package monte_carlo_tree_search.domains.battery_cell;

import common.other.RandUtils;
import monte_carlo_tree_search.interfaces.ActionInterface;
import monte_carlo_tree_search.interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.interfaces.SimulationPolicyInterface;
import monte_carlo_tree_search.interfaces.StateInterface;

import java.util.List;
import java.util.Set;

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

    @Override
    public Set<Integer> availableActionValues(StateInterface<CellVariables> state) {
        throw new RuntimeException("Not implemented");
    }


}
