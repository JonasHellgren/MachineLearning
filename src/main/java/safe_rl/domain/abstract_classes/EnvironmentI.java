package safe_rl.domain.abstract_classes;

import common.reinforcment_learning.value_classes.StepReturn;

public interface EnvironmentI<V> {
    StepReturn<V> step(StateI<V> state, Action action);
}
