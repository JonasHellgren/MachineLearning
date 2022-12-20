package mcts_spacegame.models_space;

import mcts_spacegame.generic_interfaces.ActionInterface;
import mcts_spacegame.generic_interfaces.SimulationPolicyInterface;
import mcts_spacegame.generic_interfaces.StateInterface;
import mcts_spacegame.models_space.ActionShip;
import mcts_spacegame.models_space.ShipActionSet;
import mcts_spacegame.models_space.ShipVariables;

public class AlwaysStillPolicy implements SimulationPolicyInterface<ShipVariables, ShipActionSet> {

    @Override
    public ActionInterface<ShipActionSet> chooseAction(StateInterface<ShipVariables> state) {
        return ActionShip.newStill();
    }

}
