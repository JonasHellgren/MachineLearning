package monte_carlo_tree_search.interfaces;

import java.util.Set;

public interface SimulationPolicyInterface<S, A> {

    ActionInterface<A> chooseAction(StateInterface<S> state);

    Set<A> availableActionValues(StateInterface<S> state);  //todo set

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
