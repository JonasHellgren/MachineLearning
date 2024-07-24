package maze_domain_design.domain.environment;

import maze_domain_design.domain.environment.value_objects.Action;
import maze_domain_design.domain.environment.value_objects.StepReturn;
import maze_domain_design.environments.obstacle_on_road.StateRoad;

public interface EnvironmentI<V> {
    StepReturn step(StateRoad s, Action a);
}
