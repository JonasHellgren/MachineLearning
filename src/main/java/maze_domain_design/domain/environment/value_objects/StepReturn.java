package maze_domain_design.domain.environment.value_objects;

import lombok.Builder;
import maze_domain_design.environments.obstacle_on_road.StateRoad;

@Builder
public record StepReturn(
        StateRoad sNext,
        Double reward,
        Boolean isTerminal,
        Boolean isFail
) {
}
