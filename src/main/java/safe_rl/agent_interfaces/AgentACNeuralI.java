package safe_rl.agent_interfaces;

import org.apache.commons.math3.util.Pair;

public interface AgentACNeuralI<V>  extends AgentI<V>, NeuralActorI<V>, NeuralCriticI<V> {
    Pair<Double, Double> lossActorAndCritic();
}

