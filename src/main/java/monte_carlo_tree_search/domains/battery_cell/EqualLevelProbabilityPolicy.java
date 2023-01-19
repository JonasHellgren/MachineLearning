package monte_carlo_tree_search.domains.battery_cell;

import common.RandUtils;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.SimulationPolicyInterface;
import monte_carlo_tree_search.generic_interfaces.StateInterface;

import java.util.ArrayList;
import java.util.List;

public class EqualLevelProbabilityPolicy implements SimulationPolicyInterface<CellVariables, Integer> {
    ActionInterface<Integer> actionTemplate;

    public EqualLevelProbabilityPolicy(ActionInterface<Integer> actionTemplate) {
        this.actionTemplate = actionTemplate;
    }

    @Override
    public ActionInterface<Integer> chooseAction(StateInterface<CellVariables> state) {
        ActionInterface<Integer> action=actionTemplate.copy();
        RandUtils<Integer> randUtils=new RandUtils<>();
        List<Integer> avList= new ArrayList<>(actionTemplate.applicableActions());
        action.setValue(randUtils.getRandomItemFromList(avList));
        return action;
    }
}
