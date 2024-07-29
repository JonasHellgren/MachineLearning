package safe_rl.domain.environment;

import safe_rl.domain.environment.value_objects.Action;
import safe_rl.domain.environment.aggregates.StateI;
import safe_rl.domain.environment.value_objects.StepReturn;

public interface EnvironmentI<V> {
    StepReturn<V> step(StateI<V> state, Action action);
}
