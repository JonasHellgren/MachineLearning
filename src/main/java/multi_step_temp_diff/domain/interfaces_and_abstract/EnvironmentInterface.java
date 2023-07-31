package multi_step_temp_diff.domain.interfaces_and_abstract;


import multi_step_temp_diff.models.StepReturn;

import java.util.Set;

public interface EnvironmentInterface<S> {
    StepReturn<S> step(StateInterface<S> state, int action);
    boolean isTerminalState(StateInterface<S> state);
    Set<Integer> actionSet();
    Set<StateInterface<S>>  stateSet();
}
