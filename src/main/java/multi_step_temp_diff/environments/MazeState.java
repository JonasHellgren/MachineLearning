package multi_step_temp_diff.environments;

import common.RandUtils;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import multi_step_temp_diff.interfaces_and_abstract.StateInterface;
import multi_step_temp_diff.models.StepReturn;

import java.util.function.Function;


@Getter
@AllArgsConstructor
@EqualsAndHashCode(cacheStrategy = EqualsAndHashCode.CacheStrategy.LAZY)
public class MazeState implements StateInterface<MazeVariables> {
    MazeVariables variables;

    public static Function<StateInterface<MazeVariables>,Integer> getX=(s) -> s.getVariables().x;
    public static Function<StateInterface<MazeVariables>,Integer> getY=(s) -> s.getVariables().y;


    public static MazeState newFromXY(int x, int y) {
        return new MazeState(MazeVariables.newFromXY(x,y));
    }

    public static MazeState newFromRandomPos() {
        final int randomX = RandUtils.getRandomIntNumber(0, MazeEnvironment.NOF_COLS);
        final int randomY = RandUtils.getRandomIntNumber(0, MazeEnvironment.NOF_ROWS);
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




}
