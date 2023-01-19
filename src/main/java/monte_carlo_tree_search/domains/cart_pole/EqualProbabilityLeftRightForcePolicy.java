package monte_carlo_tree_search.domains.cart_pole;

import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.SimulationPolicyInterface;
import monte_carlo_tree_search.generic_interfaces.StateInterface;

public class EqualProbabilityLeftRightForcePolicy implements SimulationPolicyInterface<CartPoleVariables, Integer> {

    public EqualProbabilityLeftRightForcePolicy() {
    }

    @Override
    public ActionInterface<Integer> chooseAction(StateInterface<CartPoleVariables> state) {
        return ActionCartPole.newRandom();
    }

}
