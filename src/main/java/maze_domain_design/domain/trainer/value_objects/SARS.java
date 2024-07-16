package maze_domain_design.domain.trainer.value_objects;

import lombok.Builder;
import maze_domain_design.domain.environment.value_objects.Action;
import maze_domain_design.domain.environment.value_objects.State;
import maze_domain_design.domain.environment.value_objects.StepReturn;

@Builder
public record SARS(
        State s,
        Action a,
        double r,
        State sNext
) {

    public static SARS ofStateActionStepReturn(State s, Action a,StepReturn sr) {
        return SARS.builder().s(s).a(a).r(sr.reward()).sNext(sr.sNext()).build();
    }
}
