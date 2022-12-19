package mcts_spacegame.policies_action;

import mcts_spacegame.enums.ShipAction;
import mcts_spacegame.generic_interfaces.StateInterface;
import mcts_spacegame.models_space.ShipVariables;
import mcts_spacegame.models_space.StateShip;

public class AlwaysStillPolicy implements SimulationPolicyInterface {

    @Override
    public ShipAction chooseAction(StateInterface<ShipVariables> state) {
        return ShipAction.still;
    }

}
