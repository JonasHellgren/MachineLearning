package maze_domain_design.domain.trainer.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import maze_domain_design.domain.environment.value_objects.Action;
import maze_domain_design.domain.environment.value_objects.StateI;
import maze_domain_design.domain.environment.value_objects.StepReturn;
import maze_domain_design.domain.trainer.value_objects.ExperienceType;
import maze_domain_design.domain.trainer.value_objects.SARS;
@AllArgsConstructor
@Getter
public class Experience<V> {
    int id;
    SARS<V> sars;
    ExperienceType type;

    public static <V> Experience<V> ofIdStateActionStepReturn(int id, StateI<V> s, Action a, StepReturn<V> sr) {
        var sars= SARS.ofStateActionStepReturn(s,a,sr);
        var expType= ExperienceType.ofStepReturn(sr);
        return new Experience<>(id,sars,expType);
    }

    public static <V> Experience<V> nonTermWithIdAndSars(int id, StateI<V> s, Action a, double r, StateI<V> sNext) {
        return new Experience<>(id,new SARS<>(s,a,r,sNext),ExperienceType.nonTerminal());
    }

    @Override
    public String toString() {
        return "time="+id+", "+sars+", "+type;
    }

}
