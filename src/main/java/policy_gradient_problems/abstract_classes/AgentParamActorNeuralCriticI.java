package policy_gradient_problems.abstract_classes;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import java.util.List;

public interface AgentParamActorNeuralCriticI<V> {

    void fitCritic(List<List<Double>> stateValuesList, List<Double> valueTarList, int nofFits);
    void changeActor(RealVector change);
    ArrayRealVector calcGradLogVector(StateI<V> state, Action action);
    List<Double> getActionProbabilities();
}
