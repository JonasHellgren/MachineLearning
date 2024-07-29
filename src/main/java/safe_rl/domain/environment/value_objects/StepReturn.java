package safe_rl.domain.environment.value_objects;

import lombok.Builder;
import safe_rl.domain.environment.aggregates.StateI;

/**
 * Return of step in environment class
 */

@Builder
public record StepReturn<V>(
        StateI<V> state,
        boolean isFail,
        boolean isTerminal,
        double reward
) {
}
