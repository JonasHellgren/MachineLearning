package maze_domain_design.domain.environment.value_objects;

import lombok.Builder;

@Builder
public record StepReturn<V>(
        StateI<V> sNext,
        Double reward,
        Boolean isTerminal,
        Boolean isFail
) {
}
