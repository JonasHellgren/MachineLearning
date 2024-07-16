package maze_domain_design.domain.environment.value_objects;

import lombok.Builder;

@Builder
public record StepReturn(
        State sNext,
        Double reward,
        Boolean isTerminal,
        Boolean isFail
) {
}
