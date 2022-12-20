package mcts_spacegame.policies_action;

import mcts_spacegame.enums.ShipActionREMOVE;
import mcts_spacegame.generic_interfaces.ActionInterface;
import mcts_spacegame.generic_interfaces.StateInterface;
import mcts_spacegame.models_space.ActionShip;
import mcts_spacegame.models_space.ShipActionSet;
import mcts_spacegame.models_space.ShipVariables;
import org.apache.commons.lang3.RandomUtils;

public class MostlyStillPolicy implements SimulationPolicyInterface<ShipVariables, ShipActionSet> {

    private static final double PROB_STILL = 0.9;
    private static final double PROB_UP_IF_NOT_STILL = 0.5;

    @Override
    public ActionInterface<ShipActionSet> chooseAction(StateInterface<ShipVariables> state) {
        double p = getRandomBetweenZeroAndOne();
        if (p < PROB_STILL) {
            return ActionShip.newStill();
        }

        p = getRandomBetweenZeroAndOne();
        return (p < PROB_UP_IF_NOT_STILL)
                ? ActionShip.newUp()
                : ActionShip.newDown();
    }

    private double getRandomBetweenZeroAndOne() {
        return RandomUtils.nextDouble(0, 1);
    }
}
