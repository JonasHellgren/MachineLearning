package mcts_spacegame.generic_interfaces;

public interface SimulationPolicyInterface<SSV,AV> {

    ActionInterface<AV> chooseAction(StateInterface<SSV> state);

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
