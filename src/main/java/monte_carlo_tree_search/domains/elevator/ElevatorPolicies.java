package monte_carlo_tree_search.domains.elevator;

import monte_carlo_tree_search.domains.cart_pole.CartPoleVariables;
import monte_carlo_tree_search.domains.cart_pole.EqualProbabilityLeftRightForcePolicy;
import monte_carlo_tree_search.generic_interfaces.SimulationPolicyInterface;

public class ElevatorPolicies {

    public static SimulationPolicyInterface<VariablesElevator, Integer> newRandomDirectionAfterStopping() {
        return new PolicyMoveUpAndDownStopEveryFloorRandomDirectionAfterStopping();
    }

    public static SimulationPolicyInterface<VariablesElevator, Integer> newRandom() {
        return new PolicyRandom();
    }

}