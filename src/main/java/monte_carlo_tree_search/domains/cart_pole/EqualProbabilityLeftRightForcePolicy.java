package monte_carlo_tree_search.domains.cart_pole;

import common.RandUtils;
import monte_carlo_tree_search.domains.models_battery_cell.CellVariables;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.SimulationPolicyInterface;
import monte_carlo_tree_search.generic_interfaces.StateInterface;

import java.util.ArrayList;
import java.util.List;

public class EqualProbabilityLeftRightForcePolicy implements SimulationPolicyInterface<CartPoleVariables, Integer> {

    public EqualProbabilityLeftRightForcePolicy() {
    }

    @Override
    public ActionInterface<Integer> chooseAction(StateInterface<CartPoleVariables> state) {
        return ActionCartPole.newRandom();
    }

}
