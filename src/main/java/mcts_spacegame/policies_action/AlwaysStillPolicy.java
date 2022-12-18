package mcts_spacegame.policies_action;

import mcts_spacegame.enums.ShipAction;
import mcts_spacegame.models_space.State;

public class AlwaysStillPolicy implements SimulationPolicyInterface {

    @Override
    public ShipAction chooseAction(State state) {
        return ShipAction.still;
    }

}
