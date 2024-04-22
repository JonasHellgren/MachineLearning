package safe_rl.domain.abstract_classes;


import safe_rl.domain.value_classes.StepReturn;

public interface EnvironmentI<V> {
    StepReturn<V> step(StateI<V> state, Action action);
}
