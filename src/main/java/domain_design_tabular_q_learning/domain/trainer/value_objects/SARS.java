package domain_design_tabular_q_learning.domain.trainer.value_objects;

import domain_design_tabular_q_learning.domain.environment.value_objects.ActionI;
import lombok.Builder;
import domain_design_tabular_q_learning.environments.obstacle_on_road.ActionRoad;
import domain_design_tabular_q_learning.domain.environment.value_objects.StateI;
import domain_design_tabular_q_learning.domain.environment.value_objects.StepReturn;

@Builder
public record SARS<V,A>(
        StateI<V> s,
        ActionI<A> a,
        double r,
        StateI<V> sNext
) {

    public static <V,A> SARS<V,A> ofStateActionStepReturn(StateI<V> s,
                                                      ActionI<A> a,
                                                      StepReturn<V> sr) {
        return SARS.<V,A>builder().s(s).a(a).r(sr.reward()).sNext(sr.sNext()).build();
    }
}
