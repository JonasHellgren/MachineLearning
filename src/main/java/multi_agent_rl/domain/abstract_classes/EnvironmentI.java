package multi_agent_rl.domain.abstract_classes;


import multi_agent_rl.domain.value_classes.StepReturn;

public interface EnvironmentI<V> {
    StepReturn<V> step(StateI<V> state, ActionJoint action);
}
