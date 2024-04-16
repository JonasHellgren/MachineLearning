package policy_gradient_problems.domain.abstract_classes;

import policy_gradient_problems.domain.value_classes.StepReturn;

public interface EnvironmentI<V> {

    StepReturn<V> step(StateI<V> state, Action action);
}
