package monte_carlo_tree_search.domains.elevator;

import monte_carlo_tree_search.interfaces.ActionInterface;
import monte_carlo_tree_search.interfaces.SimulationPolicyInterface;
import monte_carlo_tree_search.interfaces.StateInterface;

import java.util.Set;

public class PolicyRandomAtFloors implements SimulationPolicyInterface<VariablesElevator, Integer> {


    @Override
    public ActionInterface<Integer> chooseAction(StateInterface<VariablesElevator> state) {
        return null;
    }

    @Override
    public Set<Integer> availableActionValues(StateInterface<VariablesElevator> state) {
            ActionElevator actionElevator=ActionElevator.newValueDefaultRange(0);
            return actionElevator.applicableActions();
        }

}
