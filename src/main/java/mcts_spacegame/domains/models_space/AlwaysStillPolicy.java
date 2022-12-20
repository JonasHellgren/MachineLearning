package mcts_spacegame.domains.models_space;

import mcts_spacegame.generic_interfaces.ActionInterface;
import mcts_spacegame.generic_interfaces.SimulationPolicyInterface;
import mcts_spacegame.generic_interfaces.StateInterface;

public class AlwaysStillPolicy implements SimulationPolicyInterface<ShipVariables, ShipActionSet> {

    @Override
    public ActionInterface<ShipActionSet> chooseAction(StateInterface<ShipVariables> state) {
        return ActionShip.newStill();
    }

}
