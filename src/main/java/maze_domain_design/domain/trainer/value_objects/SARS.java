package maze_domain_design.domain.trainer.value_objects;

import lombok.Builder;
import maze_domain_design.domain.environment.value_objects.Action;
import maze_domain_design.domain.environment.value_objects.StateI;
import maze_domain_design.environments.obstacle_on_road.StateRoad;
import maze_domain_design.domain.environment.value_objects.StepReturn;

@Builder
public record SARS<V>(
        StateI<V> s,
        Action a,
        double r,
        StateI<V> sNext
) {

    public static <V> SARS<V> ofStateActionStepReturn(StateI<V> s, Action a, StepReturn<V> sr) {
        return SARS.<V>builder().s(s).a(a).r(sr.reward()).sNext(sr.sNext()).build();
    }
}
