package mcts_spacegame.policies_action;

import mcts_spacegame.enums.Action;
import mcts_spacegame.models_space.State;
import org.apache.commons.lang3.RandomUtils;

public class MostlyStillPolicy implements SimulationPolicyInterface {

    private static final double PROB_STILL = 0.9;
    private static final double PROB_UP_IF_NOT_STILL = 0.5;

    @Override
    public Action chooseAction(State state) {
        double p = getRandomBetweenZeroAndOne();
        if (p < PROB_STILL) {
            return Action.still;
        }

        p = getRandomBetweenZeroAndOne();
        return (p < PROB_UP_IF_NOT_STILL)
                ? Action.up
                : Action.down;
    }

    private double getRandomBetweenZeroAndOne() {
        return RandomUtils.nextDouble(0, 1);
    }
}
