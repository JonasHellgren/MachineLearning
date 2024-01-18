package policy_gradient_problems.agent_interfaces;

import org.nd4j.linalg.api.ndarray.INDArray;
import policy_gradient_problems.abstract_classes.StateI;

import java.util.List;

public interface NeuralCriticI<V> {
    void fitCritic(List<List<Double>> stateValuesList, List<Double> valueTarList);
    double getCriticOut(StateI<V> state);
}
