package monte_carlo_tree_search.domains.cart_pole;

import monte_carlo_tree_search.domains.models_battery_cell.CellVariables;
import monte_carlo_tree_search.domains.models_battery_cell.EqualLevelProbabilityPolicy;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.SimulationPolicyInterface;

public class CartPolePolicies {

    public static SimulationPolicyInterface<CartPoleVariables, Integer> newEqualProbability() {
        return new EqualProbabilityLeftRightForcePolicy();
    }

}
