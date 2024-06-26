package monte_carlo_tree_search.domains.cart_pole;

import monte_carlo_tree_search.interfaces.SimulationPolicyInterface;

public class CartPolePolicies {

    public static SimulationPolicyInterface<CartPoleVariables, Integer> newEqualProbability() {
        return new PolicyEqualProbabilityLeftRightForce();
    }

}
