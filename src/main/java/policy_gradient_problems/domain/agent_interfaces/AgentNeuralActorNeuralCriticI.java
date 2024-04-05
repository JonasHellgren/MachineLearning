package policy_gradient_problems.domain.agent_interfaces;

import org.apache.commons.math3.util.Pair;

public interface AgentNeuralActorNeuralCriticII<V>  extends AgentI<V>, NeuralActorI<V>, NeuralCriticI<V> {
    Pair<Double, Double> lossActorAndCritic();

}

