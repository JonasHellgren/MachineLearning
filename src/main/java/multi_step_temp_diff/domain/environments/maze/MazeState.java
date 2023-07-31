package multi_step_temp_diff.domain.environments.maze;

import common.RandUtils;
import lombok.Getter;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.environment_abstract.StepReturn;
import multi_step_temp_diff.domain.environment_valueobj.MazeEnvironmentSettings;

import java.util.Objects;
import java.util.function.Function;

/**
 * https://www.baeldung.com/java-custom-class-map-key
 */


@Getter
public class MazeState implements StateInterface<MazeVariables> {
    MazeVariables variables;
    private final int hashCode;

    public static Function<StateInterface<MazeVariables>,Integer> getX=(s) -> s.getVariables().x;
    public static Function<StateInterface<MazeVariables>,Integer> getY=(s) -> s.getVariables().y;

    public MazeState(MazeVariables variables) {
        this.variables = variables;
        this.hashCode=Objects.hash(variables.x,variables.y);
    }

    public static MazeState newFromXY(int x, int y) {
        return new MazeState(MazeVariables.newFromXY(x,y));
    }

    public static MazeState newFromRandomPos() {
        int randomX, randomY;
        do {
            randomX = RandUtils.getRandomIntNumber(0, MazeEnvironment.settings.nofCols());
            randomY = RandUtils.getRandomIntNumber(0, MazeEnvironment.settings.nofRows());
        } while (!MazeEnvironment.isValidStartPosition.apply(randomX,randomY));

        return new MazeState(MazeVariables.newFromXY(randomX,randomY));
    }

    @Override
    public StateInterface<MazeVariables> copy() {
        return new MazeState(variables.copy());
    }

    @Override
    public void setFromReturn(StepReturn<MazeVariables> stepReturn) {
        variables.x=MazeState.getX.apply(stepReturn.newState);
        variables.y=MazeState.getY.apply(stepReturn.newState);

    }

    @Override
    public String toString() {
        return  variables.toString();
    }

    @Override
    public boolean equals(Object otherState) {
        //check if the argument is a reference to this object
        if (otherState == this) return true;

        //check if the argument has the correct typ
        if (!(otherState instanceof MazeState)) return false;

        MazeState otherCasted = (MazeState) otherState;
        return Objects.equals(getX.apply(this), getX.apply(otherCasted))
                && Objects.equals(getY.apply(this), getY.apply(otherCasted));

    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }


}
