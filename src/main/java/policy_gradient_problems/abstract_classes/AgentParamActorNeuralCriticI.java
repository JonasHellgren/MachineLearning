package policy_gradient_problems.abstract_classes;

import java.util.List;

public interface AgentParamActorNeuralCriticI<V> extends AgentParamActorI<V> {
    void fitCritic(List<List<Double>> stateValuesList, List<Double> valueTarList, int nofFits);
    double getCriticOut(StateI<V> state);

}
