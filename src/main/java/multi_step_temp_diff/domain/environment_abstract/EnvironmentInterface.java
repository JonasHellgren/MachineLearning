package multi_step_temp_diff.domain.environment_abstract;


import multi_step_temp_diff.domain.agent_abstract.StateInterface;

import java.util.Set;

public interface EnvironmentInterface<S> {
    StepReturn<S> step(StateInterface<S> state, int action);
    boolean isTerminalState(StateInterface<S> state);
    Set<Integer> actionSet();
    Set<StateInterface<S>>  stateSet();
}
