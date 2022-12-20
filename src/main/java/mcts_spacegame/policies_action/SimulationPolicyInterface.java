package mcts_spacegame.policies_action;

import mcts_spacegame.enums.ShipActionREMOVE;
import mcts_spacegame.generic_interfaces.ActionInterface;
import mcts_spacegame.generic_interfaces.StateInterface;
import mcts_spacegame.models_space.ActionShip;
import mcts_spacegame.models_space.ShipActionSet;
import mcts_spacegame.models_space.ShipVariables;

public interface SimulationPolicyInterface<SSV,AV> {

    ActionInterface<AV> chooseAction(StateInterface<SSV> state);

    static SimulationPolicyInterface<ShipVariables, ShipActionSet> newMostlyStill() {
        return new MostlyStillPolicy();
    }

    static SimulationPolicyInterface<ShipVariables, ShipActionSet> newAlwaysStill() {
        return new AlwaysStillPolicy();
    }

    static SimulationPolicyInterface<ShipVariables, ShipActionSet> newEqualProbability() {
        return new EqualActionProbabilityPolicy();
    }
}
