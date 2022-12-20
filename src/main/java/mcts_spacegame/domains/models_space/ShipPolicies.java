package mcts_spacegame.domains.models_space;

import mcts_spacegame.generic_interfaces.SimulationPolicyInterface;

public class ShipPolicies {

    public static SimulationPolicyInterface<ShipVariables, ShipActionSet> newMostlyStill() {
        return new MostlyStillPolicy();
    }

    public static SimulationPolicyInterface<ShipVariables, ShipActionSet> newAlwaysStill() {
        return new AlwaysStillPolicy();
    }

    public static SimulationPolicyInterface<ShipVariables, ShipActionSet> newEqualProbability() {
        return new EqualActionProbabilityPolicy();
    }
}
