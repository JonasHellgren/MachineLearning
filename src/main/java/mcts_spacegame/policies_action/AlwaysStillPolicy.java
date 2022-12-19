package mcts_spacegame.policies_action;

import mcts_spacegame.enums.ShipAction;
import mcts_spacegame.models_space.StateShip;

public class AlwaysStillPolicy implements SimulationPolicyInterface {

    @Override
    public ShipAction chooseAction(StateShip state) {
        return ShipAction.still;
    }

}
