package policy_gradient_problems.domain.abstract_classes;

import common.reinforcment_learning.value_classes.StepReturn;

public interface EnvironmentI<V> {

    StepReturn<V> step(StateI<V> state, Action action);
}
