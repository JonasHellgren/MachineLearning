package mcts_spacegame.policies_action;

import common.RandUtils;
import mcts_spacegame.enums.Action;
import mcts_spacegame.models_space.State;

public class EqualActionProbabilityPolicy implements SimulationPolicyInterface {
    @Override
    public Action chooseAction(State state) {
        RandUtils<Action> randUtils=new RandUtils<>();
        return randUtils.getRandomItemFromList(Action.applicableActions());
     }
}
