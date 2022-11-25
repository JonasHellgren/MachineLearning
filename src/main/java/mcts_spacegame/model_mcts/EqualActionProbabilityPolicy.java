package mcts_spacegame.model_mcts;

import mcts_spacegame.enums.Action;
import mcts_spacegame.models_space.State;
import org.apache.commons.lang3.RandomUtils;

import java.util.Random;

public class EqualActionProbabilityPolicy implements  SimulationPolicyInterface {
    @Override
    public Action chooseAction(State state) {
        int randomIndex=RandomUtils.nextInt(0,Action.applicableActions().size());
        return Action.applicableActions().get(randomIndex);
    }
}
