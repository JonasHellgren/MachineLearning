package monte_carlo_tree_search.domains.battery_cell;

import common.other.RandUtilsML;
import monte_carlo_tree_search.interfaces.ActionInterface;
import monte_carlo_tree_search.interfaces.SimulationPolicyInterface;
import monte_carlo_tree_search.interfaces.StateInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class EqualLevelProbabilityPolicy implements SimulationPolicyInterface<CellVariables, Integer> {
    ActionInterface<Integer> actionTemplate;

    public EqualLevelProbabilityPolicy(ActionInterface<Integer> actionTemplate) {
        this.actionTemplate = actionTemplate;
    }

    @Override
    public ActionInterface<Integer> chooseAction(StateInterface<CellVariables> state) {
        ActionInterface<Integer> action=actionTemplate.copy();
        RandUtilsML<Integer> randUtils=new RandUtilsML<>();
        List<Integer> avList= new ArrayList<>(actionTemplate.applicableActions());
        action.setValue(randUtils.getRandomItemFromList(avList));
        return action;
    }


    @Override
    public Set<Integer> availableActionValues(StateInterface<CellVariables> state) {
        throw new RuntimeException("Not implemented");
    }

}
