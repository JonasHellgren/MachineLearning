package safe_rl.environments.buying_electricity;

import lombok.Builder;
import safe_rl.domain.abstract_classes.StateI;

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
