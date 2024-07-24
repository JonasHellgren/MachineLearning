package domain_design_tabular_q_learning.domain.trainer.entities;

import domain_design_tabular_q_learning.domain.environment.value_objects.ActionI;
import lombok.AllArgsConstructor;
import lombok.Getter;
import domain_design_tabular_q_learning.environments.obstacle_on_road.ActionRoad;
import domain_design_tabular_q_learning.domain.environment.value_objects.StateI;
import domain_design_tabular_q_learning.domain.environment.value_objects.StepReturn;
import domain_design_tabular_q_learning.domain.trainer.value_objects.ExperienceType;
import domain_design_tabular_q_learning.domain.trainer.value_objects.SARS;
@AllArgsConstructor
@Getter
public class Experience<V,A> {
    int id;
    SARS<V,A> sars;
    ExperienceType type;

    public static <V,A> Experience<V,A> ofIdStateActionStepReturn(int id, StateI<V> s, ActionI<A> a, StepReturn<V> sr) {
        var sars= SARS.ofStateActionStepReturn(s,a,sr);
        var expType= ExperienceType.ofStepReturn(sr);
        return new Experience<>(id,sars,expType);
    }

    public static <V,A> Experience<V,A> nonTermWithIdAndSars(int id, StateI<V> s, ActionI<A> a, double r, StateI<V> sNext) {
        return new Experience<>(id,new SARS<>(s,a,r,sNext),ExperienceType.nonTerminal());
    }

    @Override
    public String toString() {
        return "time="+id+", "+sars+", "+type;
    }

}
