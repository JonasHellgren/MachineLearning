package monte_carlo_tree_search.domains.cart_pole;

import monte_carlo_tree_search.domains.battery_cell.CellVariables;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.SimulationPolicyInterface;
import monte_carlo_tree_search.generic_interfaces.StateInterface;

import java.util.List;
import java.util.Set;

public class PolicyEqualProbabilityLeftRightForce implements SimulationPolicyInterface<CartPoleVariables, Integer> {

    public PolicyEqualProbabilityLeftRightForce() {
    }

    @Override
    public ActionInterface<Integer> chooseAction(StateInterface<CartPoleVariables> state) {
        return ActionCartPole.newRandom();
    }

    @Override
    public Set<Integer> availableActionValues(StateInterface<CartPoleVariables> state) {
        ActionInterface<Integer>  actionTemplate=  ActionCartPole.newRandom();
        return actionTemplate.applicableActions();
    }

}
