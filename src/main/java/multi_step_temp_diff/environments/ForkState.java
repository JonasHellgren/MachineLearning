package multi_step_temp_diff.environments;

import common.RandUtils;
import lombok.Getter;
import multi_step_temp_diff.interfaces_and_abstract.StateInterface;
import multi_step_temp_diff.models.StepReturn;

@Getter
public class ForkState implements StateInterface<ForkVariables> {

    ForkVariables variables;

    public ForkState(ForkVariables variables) {
        this.variables = variables;
    }

    public static ForkState newFromPos(int position) {
        return new ForkState(ForkVariables.newFromPos(position));
    }

    public static ForkState newFromRandomPos() {
        final int randomPos = RandUtils.getRandomIntNumber(0, ForkEnvironment.NOF_STATES);
        return new ForkState(ForkVariables.newFromPos(randomPos));
    }

    @Override
    public StateInterface<ForkVariables> copy() {
        return new ForkState(variables.copy());
    }

//    @Override
    public void setFromReturn(StepReturn<ForkVariables> stepReturn) {

    }
}
