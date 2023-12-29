package policy_gradient_problems.common_generic;

import lombok.Builder;
import policy_gradient_problems.abstract_classes.StateI;

@Builder
public record StepReturn<V>(
        StateI<V> state,
        boolean isFail,
        boolean isTerminal,
        double reward
) {
}
