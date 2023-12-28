package policy_gradient_problems.abstract_classes;

import org.nd4j.linalg.api.ndarray.INDArray;

public interface AgentNeuralActorCriticInterface {
    void fitActor(INDArray in, INDArray out);
    void fitCritic(INDArray in, double value);
}
