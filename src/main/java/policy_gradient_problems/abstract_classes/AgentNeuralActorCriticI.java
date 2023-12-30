package policy_gradient_problems.abstract_classes;

import org.nd4j.linalg.api.ndarray.INDArray;
import policy_gradient_problems.the_problems.cart_pole.VariablesPole;

public interface AgentNeuralActorCriticI {
    void fitActor(INDArray in, INDArray out);
    void fitCritic(INDArray in, double value);

}
