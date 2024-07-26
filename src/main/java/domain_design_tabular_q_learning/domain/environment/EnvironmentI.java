package domain_design_tabular_q_learning.domain.environment;

import domain_design_tabular_q_learning.domain.environment.value_objects.ActionI;
import domain_design_tabular_q_learning.domain.environment.value_objects.StateI;
import domain_design_tabular_q_learning.domain.environment.value_objects.StepReturn;

public interface EnvironmentI<V,A,P> {
    StepReturn<V> step(StateI<V> s, ActionI<A> a);
    StateI<V> getStartState();
    P getProperties();
    void setProperties(P p);
    ActionI<A>[] actions();
    ActionI<A> randomAction();
}
