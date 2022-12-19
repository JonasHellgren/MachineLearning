package mcts_spacegame.policies_action;

import mcts_spacegame.enums.ShipAction;
import mcts_spacegame.models_space.StateShip;
import org.apache.commons.lang3.RandomUtils;

public class MostlyStillPolicy implements SimulationPolicyInterface {

    private static final double PROB_STILL = 0.9;
    private static final double PROB_UP_IF_NOT_STILL = 0.5;

    @Override
    public ShipAction chooseAction(StateShip state) {
        double p = getRandomBetweenZeroAndOne();
        if (p < PROB_STILL) {
            return ShipAction.still;
        }

        p = getRandomBetweenZeroAndOne();
        return (p < PROB_UP_IF_NOT_STILL)
                ? ShipAction.up
                : ShipAction.down;
    }

    private double getRandomBetweenZeroAndOne() {
        return RandomUtils.nextDouble(0, 1);
    }
}
