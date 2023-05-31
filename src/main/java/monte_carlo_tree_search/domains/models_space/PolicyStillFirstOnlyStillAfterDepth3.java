package monte_carlo_tree_search.domains.models_space;

import monte_carlo_tree_search.interfaces.ActionInterface;
import monte_carlo_tree_search.interfaces.SimulationPolicyInterface;
import monte_carlo_tree_search.interfaces.StateInterface;

import java.util.Collections;
import java.util.Set;

public class PolicyStillFirstOnlyStillAfterDepth3 implements SimulationPolicyInterface<ShipVariables, ShipActionSet> {

    ActionInterface<ShipActionSet> actionTemplate;

    public PolicyStillFirstOnlyStillAfterDepth3(ActionInterface<ShipActionSet> actionTemplate) {
        this.actionTemplate = actionTemplate;
    }

    @Override
    public ActionInterface<ShipActionSet> chooseAction(StateInterface<ShipVariables> state) {
        return ActionShip.newStill();
    }

    @Override
    public Set<ShipActionSet> availableActionValues(StateInterface<ShipVariables> state) {

        return (state.getVariables().x <= 3)
                ? actionTemplate.applicableActions()
                : Collections.singleton(ShipActionSet.still);
    }
}
