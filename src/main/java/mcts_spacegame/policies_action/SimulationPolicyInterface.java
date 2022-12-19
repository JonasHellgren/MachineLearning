package mcts_spacegame.policies_action;

import mcts_spacegame.enums.ShipActionREMOVE;
import mcts_spacegame.generic_interfaces.ActionInterface;
import mcts_spacegame.generic_interfaces.StateInterface;
import mcts_spacegame.models_space.ShipActionSet;
import mcts_spacegame.models_space.ShipVariables;

public interface SimulationPolicyInterface {

    ActionInterface<ShipActionSet> chooseAction(StateInterface<ShipVariables> state);

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
