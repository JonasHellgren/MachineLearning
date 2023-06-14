package multi_step_temp_diff.environments;

import lombok.Getter;
import multi_step_temp_diff.interfaces_and_abstract.StateInterface;
import multi_step_temp_diff.models.StepReturn;

@Getter
public class ForkState implements StateInterface<ForkVariables> {

    ForkVariables variables;

    public ForkState(ForkVariables variables) {
        this.variables = variables;
    }

    @Override
    public StateInterface<ForkVariables> copy() {
        return new ForkState(variables.copy());
    }

//    @Override
    public void setFromReturn(StepReturn stepReturn) {

    }
}
