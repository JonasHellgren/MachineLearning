package maze_domain_design.domain.trainer.value_objects;

import lombok.Builder;
import maze_domain_design.domain.environment.value_objects.Action;
import maze_domain_design.environments.obstacle_on_road.StateRoad;
import maze_domain_design.domain.environment.value_objects.StepReturn;

@Builder
public record SARS(
        StateRoad s,
        Action a,
        double r,
        StateRoad sNext
) {

    public static SARS ofStateActionStepReturn(StateRoad s, Action a, StepReturn sr) {
        return SARS.builder().s(s).a(a).r(sr.reward()).sNext(sr.sNext()).build();
    }
}
