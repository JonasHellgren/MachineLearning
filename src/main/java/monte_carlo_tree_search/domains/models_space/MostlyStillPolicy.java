package monte_carlo_tree_search.domains.models_space;

import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.SimulationPolicyInterface;
import monte_carlo_tree_search.generic_interfaces.StateInterface;
import org.apache.commons.lang3.RandomUtils;

import java.util.List;
import java.util.Set;

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

    @Override
    public Set<ShipActionSet> availableActionValues(StateInterface<ShipVariables> state) {
        throw new RuntimeException("Not implemented");
    }

    private double getRandomBetweenZeroAndOne() {
        return RandomUtils.nextDouble(0, 1);
    }
}
