package policy_gradient_problems.abstract_classes;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import policy_gradient_problems.the_problems.cart_pole.VariablesPole;

import java.util.List;

public interface AgentParamActorNeuralCriticI<V> extends AgentI<VariablesPole> {

    void fitCritic(List<List<Double>> stateValuesList, List<Double> valueTarList, int nofFits);
    double getCriticOut(StateI<VariablesPole> state);
    void changeActor(RealVector change);
    ArrayRealVector calcGradLogVector(StateI<V> state, Action action);
    List<Double> getActionProbabilities();
}
