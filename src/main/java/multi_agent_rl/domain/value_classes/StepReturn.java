package multi_agent_rl.domain.value_classes;

import lombok.Builder;
import multi_agent_rl.domain.abstract_classes.StateI;

/**
 * Return of step in environment class
 */

@Builder
public record StepReturn<V,O>(
        StateI<V,O> state,
        boolean isFail,
        boolean isTerminal,
        double reward
) {
}
