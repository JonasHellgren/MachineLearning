package policy_gradient_problems.domain.agent_interfaces;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import policy_gradient_problems.domain.abstract_classes.Action;
import policy_gradient_problems.domain.abstract_classes.StateI;

import java.util.List;

public interface ParamActorI<V> {
    void changeActor(RealVector change);
    ArrayRealVector calcGradLogVector(StateI<V> state, Action action);
}
