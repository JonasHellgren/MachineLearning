package monte_carlo_tree_search.domains.models_battery_cell;

import common.RandUtils;
import monte_carlo_tree_search.classes.StepReturnGeneric;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.generic_interfaces.SimulationPolicyInterface;
import monte_carlo_tree_search.generic_interfaces.StateInterface;

import java.util.ArrayList;
import java.util.List;

public class RandomFeasiblePolicy  implements SimulationPolicyInterface<CellVariables, Integer> {

    ActionInterface<Integer> actionTemplate;
    EnvironmentGenericInterface<CellVariables, Integer> environment;

    public RandomFeasiblePolicy(ActionInterface<Integer> actionTemplate,
                                EnvironmentGenericInterface<CellVariables, Integer> environment) {
        this.actionTemplate = actionTemplate;
        this.environment = environment;
    }

    @Override
    public ActionInterface<Integer> chooseAction(StateInterface<CellVariables> state) {
        ActionInterface<Integer> action=actionTemplate.copy();

        List<Integer> feasibleValueList=new ArrayList<>();
        for (Integer av: action.applicableActions())  {
            action.setValue(av);
            StepReturnGeneric<CellVariables> sr=environment.step(action,state);
            if (!sr.isFail) {
                feasibleValueList.add(av);
            }
        }

        RandUtils<Integer> randUtils=new RandUtils<>();
        int av=(feasibleValueList.isEmpty())
                ? action.nonApplicableAction()
                :randUtils.getRandomItemFromList(feasibleValueList);
        action.setValue(av);
        return action;
    }
}
