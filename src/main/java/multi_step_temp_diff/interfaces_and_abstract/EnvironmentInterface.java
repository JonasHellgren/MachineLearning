package multi_step_temp_diff.interfaces_and_abstract;


import multi_step_temp_diff.models.StepReturn;

import java.util.Set;

public interface EnvironmentInterface<S> {
    StepReturn step(StateInterface<S> state, int action);
    boolean isTerminalState(StateInterface<S> state);
    Set<Integer> actionSet();
    Set<S> stateSet();
}
