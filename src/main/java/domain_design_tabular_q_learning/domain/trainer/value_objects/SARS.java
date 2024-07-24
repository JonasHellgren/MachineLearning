package domain_design_tabular_q_learning.domain.trainer.value_objects;

import lombok.Builder;
import domain_design_tabular_q_learning.domain.environment.value_objects.Action;
import domain_design_tabular_q_learning.domain.environment.value_objects.StateI;
import domain_design_tabular_q_learning.domain.environment.value_objects.StepReturn;

@Builder
public record SARS<V>(
        StateI<V> s,
        Action a,
        double r,
        StateI<V> sNext
) {

    public static <V> SARS<V> ofStateActionStepReturn(StateI<V> s,
                                                      Action a,
                                                      StepReturn<V> sr) {
        return SARS.<V>builder().s(s).a(a).r(sr.reward()).sNext(sr.sNext()).build();
    }
}
