package safe_rl.domain.agent.interfaces;

import policy_gradient_problems.domain.abstract_classes.StateI;

import java.util.List;

public interface NeuralCriticI<V> {
    void fitCritic(List<List<Double>> stateValuesList, List<Double> valueTarList);
    double criticOut(StateI<V> state);
}
