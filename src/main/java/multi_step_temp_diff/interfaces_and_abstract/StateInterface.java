package multi_step_temp_diff.interfaces_and_abstract;

import multi_step_temp_diff.models.StepReturn;

public interface StateInterface<S> {
    S getVariables();
    StateInterface<S> copy();
    void setFromReturn(StepReturn<S> stepReturn);
}
