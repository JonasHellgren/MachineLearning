package monte_carlo_tree_search.domains.models_space;

import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.SimulationPolicyInterface;
import monte_carlo_tree_search.generic_interfaces.StateInterface;

import java.util.List;
import java.util.Set;

public class AlwaysStillPolicy implements SimulationPolicyInterface<ShipVariables, ShipActionSet> {

    @Override
    public ActionInterface<ShipActionSet> chooseAction(StateInterface<ShipVariables> state) {
        return ActionShip.newStill();
    }


    @Override
    public Set<ShipActionSet> availableActionValues(StateInterface<ShipVariables> state) {
        throw new RuntimeException("Not implemented");
    }

}
