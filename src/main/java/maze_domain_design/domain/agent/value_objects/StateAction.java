package maze_domain_design.domain.agent.value_objects;

import maze_domain_design.domain.environment.value_objects.Action;
import maze_domain_design.domain.environment.value_objects.State;

public record StateAction(State state, Action action) {
    public static StateAction of(State s, Action a){
        return new StateAction(s,a);
    }
}