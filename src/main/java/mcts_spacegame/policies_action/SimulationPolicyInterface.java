package mcts_spacegame.policies_action;

import mcts_spacegame.enums.ShipAction;
import mcts_spacegame.generic_interfaces.StateInterface;
import mcts_spacegame.models_space.ShipVariables;
import mcts_spacegame.models_space.StateShip;

public interface SimulationPolicyInterface {

    ShipAction chooseAction(StateInterface<ShipVariables> state);

    static SimulationPolicyInterface newMostlyStill() {
        return new MostlyStillPolicy();
    }

    static SimulationPolicyInterface newAlwaysStill() {
        return new AlwaysStillPolicy();
    }

    static SimulationPolicyInterface newEqualProbability() {
        return new EqualActionProbabilityPolicy();
    }
}
