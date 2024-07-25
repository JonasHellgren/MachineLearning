package domain_design_tabular_q_learning.domain.environment;

import domain_design_tabular_q_learning.domain.environment.value_objects.ActionI;
import domain_design_tabular_q_learning.domain.environment.value_objects.StateI;
import domain_design_tabular_q_learning.domain.environment.value_objects.StepReturn;
import domain_design_tabular_q_learning.environments.avoid_obstacle.PropertiesRoad;

public interface EnvironmentI<V,A> {
    StepReturn<V> step(StateI<V> s, ActionI<A> a);
    StateI<V> getStartState();
    PropertiesRoad getProperties();
    void setProperties(PropertiesRoad p);
    ActionI<A>[] actions();
    ActionI<A> randomAction();
}
