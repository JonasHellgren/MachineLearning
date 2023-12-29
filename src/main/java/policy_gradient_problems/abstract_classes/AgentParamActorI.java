package policy_gradient_problems.abstract_classes;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import policy_gradient_problems.common.TabularValueFunction;

import java.util.List;

public interface AgentParamActorI<V> extends AgentI<V> {
    TabularValueFunction getCriticParams();
    void changeActor(RealVector change);
    ArrayRealVector calcGradLogVector(StateI<V> state,Action action);
    List<Double> getActionProbabilities();
}
