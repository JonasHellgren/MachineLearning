package monte_carlo_tree_search.domains.models_space;

import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.SimulationPolicyInterface;

public class ShipPolicies {

    public static SimulationPolicyInterface<ShipVariables, ShipActionSet> newMostlyStill() {
        return new PolicyMostlyStill();
    }

    public static SimulationPolicyInterface<ShipVariables, ShipActionSet> newAlwaysStill() {
        return new PolicyAlwaysStill();
    }

    public static SimulationPolicyInterface<ShipVariables, ShipActionSet> newEqualProbability(
            ActionInterface<ShipActionSet> actionTemplate) {
        return new PolicyEqualActionProbability(actionTemplate.applicableActions());
    }

    public static SimulationPolicyInterface<ShipVariables,ShipActionSet> newOnlyStillAfterDepth3 (
            ActionInterface<ShipActionSet> actionTemplate) {
        return new PolicyStillFirstOnlyStillAfterDepth3(actionTemplate);
    }

}
