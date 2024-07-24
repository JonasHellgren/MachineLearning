package maze_domain_design.domain.agent.value_objects;

import maze_domain_design.domain.environment.value_objects.Action;
import maze_domain_design.domain.environment.value_objects.StateI;
import maze_domain_design.environments.obstacle_on_road.StateRoad;
import maze_domain_design.domain.trainer.entities.Experience;

public record StateAction<V>(StateI<V> state, Action action) {
    public static <V> StateAction<V> of(StateI<V> s, Action a){
        return new StateAction<>(s,a);
    }

    public static <V> StateAction<V> ofSars(Experience<V> e){
        return new StateAction<>(e.getSars().s(), e.getSars().a());
    }

}