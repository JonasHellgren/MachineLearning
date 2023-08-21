package multi_step_temp_diff.domain.environments.fork;

import common.RandUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.environment_abstract.StepReturn;
import multi_step_temp_diff.domain.environment_valueobj.ForkEnvironmentSettings;

import java.util.function.Function;


/**
 * To enable get when put in hashmap
 * https://www.baeldung.com/java-custom-class-map-key
 */

@Getter
@EqualsAndHashCode(cacheStrategy = EqualsAndHashCode.CacheStrategy.LAZY)
public class ForkState implements StateInterface<ForkVariables> {

    ForkVariables variables;

    public static Function<StateInterface<ForkVariables>,Integer> getPos=(s) -> s.getVariables().position;

    public ForkState(ForkVariables variables) {
        this.variables = variables;
    }

    public static ForkState newFromPos(int position) {
        return new ForkState(ForkVariables.newFromPos(position));
    }

    public static ForkState newFromRandomPos() {
        ForkEnvironmentSettings envSettings=ForkEnvironmentSettings.getDefault();  //slows down
        final int randomPos = RandUtils.getRandomIntNumber(0, envSettings.nofStates()-1);
        return new ForkState(ForkVariables.newFromPos(randomPos));
    }

    @Override
    public StateInterface<ForkVariables> copy() {
        return new ForkState(variables.copy());
    }

    @Override
    public void setFromReturn(StepReturn<ForkVariables> stepReturn) {
        variables.position=ForkState.getPos.apply(stepReturn.newState);
    }

    @Override
    public String toString() {
        return  variables.toString();
    }

}
