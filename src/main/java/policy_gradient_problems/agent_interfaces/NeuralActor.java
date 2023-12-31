package policy_gradient_problems.agent_interfaces;

import org.nd4j.linalg.api.ndarray.INDArray;

public interface NeuralActor<V> {
    void fitActor(INDArray in, INDArray out);

}
