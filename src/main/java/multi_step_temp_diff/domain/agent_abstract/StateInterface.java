package multi_step_temp_diff.domain.agent_abstract;

import multi_step_temp_diff.domain.environment_abstract.StepReturn;

public interface StateInterface<S> {
    S getVariables();
    StateInterface<S> copy();
    void setFromReturn(StepReturn<S> stepReturn);
}
