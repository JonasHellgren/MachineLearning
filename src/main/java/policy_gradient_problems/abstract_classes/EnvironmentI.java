package policy_gradient_problems.abstract_classes;

import policy_gradient_problems.common_generic.StepReturn;

public interface EnvironmentI<V> {

    StepReturn<V> step(Action action);
}
