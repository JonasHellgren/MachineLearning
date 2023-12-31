package policy_gradient_problems.common_generic;

import lombok.Builder;
import policy_gradient_problems.abstract_classes.StateI;

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
