package policy_gradient_problems.agent_interfaces;

import org.nd4j.linalg.api.ndarray.INDArray;

import java.util.List;

public interface NeuralActor<V> {
    void fitActor(List<Double> in, List<Double> out);
}
