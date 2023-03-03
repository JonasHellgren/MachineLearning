package monte_carlo_tree_search.generic_interfaces;

import monte_carlo_tree_search.domains.elevator.VariablesElevator;

import java.util.List;
import java.util.Set;

public interface SimulationPolicyInterface<SSV,AV> {

    ActionInterface<AV> chooseAction(StateInterface<SSV> state);

    Set<AV> availableActionValues(StateInterface<SSV> state);  //todo set

    /*
    static SimulationPolicyInterface<ShipVariables, ShipActionSet> newMostlyStill() {
        return new MostlyStillPolicy();
    }

    static SimulationPolicyInterface<ShipVariables, ShipActionSet> newAlwaysStill() {
        return new AlwaysStillPolicy();
    }

    static SimulationPolicyInterface<ShipVariables, ShipActionSet> newEqualProbability() {
        return new EqualActionProbabilityPolicy();
    } */
}
