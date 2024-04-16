package safe_rl.domain.abstract_classes;

import common.reinforcment_learning.value_classes.StepReturn;
import policy_gradient_problems.domain.abstract_classes.Action;
import policy_gradient_problems.domain.abstract_classes.StateI;

public interface EnvironmentI<V> {
    StepReturn<V> step(StateI<V> state, Action action);
}
