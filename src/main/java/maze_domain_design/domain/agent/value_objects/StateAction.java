package maze_domain_design.domain.agent.value_objects;

import maze_domain_design.domain.environment.value_objects.Action;
import maze_domain_design.domain.environment.value_objects.State;
import maze_domain_design.domain.trainer.entities.Experience;

public record StateAction(State state, Action action) {
    public static StateAction of(State s, Action a){
        return new StateAction(s,a);
    }

    public static StateAction ofSars(Experience e){
        return new StateAction(e.getSars().s(), e.getSars().a());
    }

}