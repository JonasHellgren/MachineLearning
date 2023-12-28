package policy_gradient_problems.abstract_classes;

import org.nd4j.linalg.api.ndarray.INDArray;

public interface AgentNeuralActorInterface
{
    void fitActor(INDArray in, INDArray out);
}
