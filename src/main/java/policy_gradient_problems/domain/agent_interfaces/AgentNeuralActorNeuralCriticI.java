package policy_gradient_problems.domain.agent_interfaces;

import org.apache.commons.math3.util.Pair;

public interface AgentNeuralActorNeuralCriticI<V>  extends AgentI<V>, NeuralActor<V>, NeuralCriticI<V> {
    Pair<Double, Double> lossActorAndCritic();

}

