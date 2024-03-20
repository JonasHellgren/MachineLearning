package policy_gradient_problems.domain.value_classes;

import lombok.Builder;
import policy_gradient_problems.domain.abstract_classes.StateI;

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
