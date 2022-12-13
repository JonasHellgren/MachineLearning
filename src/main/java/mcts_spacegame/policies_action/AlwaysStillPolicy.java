package mcts_spacegame.policies_action;

import mcts_spacegame.enums.Action;
import mcts_spacegame.models_space.State;

public class AlwaysStillPolicy implements SimulationPolicyInterface {

    @Override
    public Action chooseAction(State state) {
        return Action.still;
    }

}
