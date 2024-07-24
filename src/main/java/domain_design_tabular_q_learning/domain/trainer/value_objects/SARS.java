package domain_design_tabular_q_learning.domain.trainer.value_objects;

import lombok.Builder;
import domain_design_tabular_q_learning.environments.obstacle_on_road.ActionRoad;
import domain_design_tabular_q_learning.domain.environment.value_objects.StateI;
import domain_design_tabular_q_learning.domain.environment.value_objects.StepReturn;

@Builder
public record SARS<V>(
        StateI<V> s,
        ActionRoad a,
        double r,
        StateI<V> sNext
) {

    public static <V> SARS<V> ofStateActionStepReturn(StateI<V> s,
                                                      ActionRoad a,
                                                      StepReturn<V> sr) {
        return SARS.<V>builder().s(s).a(a).r(sr.reward()).sNext(sr.sNext()).build();
    }
}
