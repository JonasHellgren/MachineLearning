package mcts_spacegame.policies_action;

import mcts_spacegame.enums.ShipAction;
import mcts_spacegame.models_space.State;

public interface SimulationPolicyInterface {

    ShipAction chooseAction(State state);

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
