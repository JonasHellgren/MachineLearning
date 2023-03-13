package monte_carlo_tree_search.domains.models_space;

import monte_carlo_tree_search.interfaces.ActionInterface;
import monte_carlo_tree_search.interfaces.SimulationPolicyInterface;
import monte_carlo_tree_search.interfaces.StateInterface;

import java.util.Set;

public class PolicyAlwaysStill implements SimulationPolicyInterface<ShipVariables, ShipActionSet> {

    @Override
    public ActionInterface<ShipActionSet> chooseAction(StateInterface<ShipVariables> state) {
        return ActionShip.newStill();
    }

    @Override
    public Set<ShipActionSet> availableActionValues(StateInterface<ShipVariables> state) {
        ActionInterface<ShipActionSet> actionTemplate=new ActionShip(ShipActionSet.notApplicable);
        return actionTemplate.applicableActions();
    }

}
