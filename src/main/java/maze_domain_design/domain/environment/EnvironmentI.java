package maze_domain_design.domain.environment;

import maze_domain_design.domain.environment.value_objects.Action;
import maze_domain_design.domain.environment.value_objects.StateI;
import maze_domain_design.domain.environment.value_objects.StepReturn;
import maze_domain_design.environments.obstacle_on_road.GridVariables;
import maze_domain_design.environments.obstacle_on_road.PropertiesRoad;
import weka.core.EnvironmentProperties;

public interface EnvironmentI<V> {
    StepReturn<V> step(StateI<V> s, Action a);
    StateI<V> getStartState();
    PropertiesRoad getProperties();
}
