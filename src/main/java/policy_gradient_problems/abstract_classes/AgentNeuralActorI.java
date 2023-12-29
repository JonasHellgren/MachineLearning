package policy_gradient_problems.abstract_classes;

import org.nd4j.linalg.api.ndarray.INDArray;

public interface AgentNeuralActorI<V> extends AgentI<V>
{
    void fitActor(INDArray in, INDArray out);
}
