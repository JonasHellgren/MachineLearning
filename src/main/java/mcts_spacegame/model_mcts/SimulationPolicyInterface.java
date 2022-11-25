package mcts_spacegame.model_mcts;

import mcts_spacegame.enums.Action;
import mcts_spacegame.models_space.State;

public interface SimulationPolicyInterface {

    Action chooseAction(State state);

    static SimulationPolicyInterface newMostlyStill() {
        return new MostlyStillPolicy();
    }

    static SimulationPolicyInterface newEqualProbability() {
        return new EqualActionProbabilityPolicy();
    }


}
