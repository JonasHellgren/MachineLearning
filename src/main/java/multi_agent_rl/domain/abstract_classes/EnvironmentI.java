package multi_agent_rl.domain.abstract_classes;


import multi_agent_rl.domain.value_classes.StepReturn;

public interface EnvironmentI<V,O> {
    StepReturn<V,O> step(StateI<V,O> state, ActionJoint action);
}
