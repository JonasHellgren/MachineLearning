package monte_carlo_tree_search.domains.elevator;

import monte_carlo_tree_search.generic_interfaces.SimulationPolicyInterface;

public class ElevatorPolicies {

    public static SimulationPolicyInterface<VariablesElevator, Integer> newRandomDirectionAfterStopping() {
        return new PolicyMoveUpAndDownStopEveryFloorRandomDirectionAfterStopping();
    }

    public static SimulationPolicyInterface<VariablesElevator, Integer> newNotUpIfLowSoE() {
       return new PolicyRandomDirectionAfterFloorIfOkSoENotUpIfLowSoE();
    }

    public static SimulationPolicyInterface<VariablesElevator, Integer> newRandom() {
        return new PolicyRandom();
    }

}
