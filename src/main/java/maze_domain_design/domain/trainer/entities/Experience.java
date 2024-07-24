package maze_domain_design.domain.trainer.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import maze_domain_design.domain.environment.value_objects.Action;
import maze_domain_design.environments.obstacle_on_road.StateRoad;
import maze_domain_design.domain.environment.value_objects.StepReturn;
import maze_domain_design.domain.trainer.value_objects.ExperienceType;
import maze_domain_design.domain.trainer.value_objects.SARS;
@AllArgsConstructor
@Getter
public class Experience {
    int id;
    SARS sars;
    ExperienceType type;

    public static Experience ofIdStateActionStepReturn(int id, StateRoad s, Action a, StepReturn sr) {
        var sars= SARS.ofStateActionStepReturn(s,a,sr);
        var expType= ExperienceType.ofStepReturn(sr);
        return new Experience(id,sars,expType);
    }

    public static Experience nonTermWithIdAndSars(int id, StateRoad s, Action a, double r, StateRoad sNext) {
        return new Experience(id,new SARS(s,a,r,sNext),ExperienceType.nonTerminal());
    }

    @Override
    public String toString() {
        return "time="+id+", "+sars+", "+type;
    }

}
