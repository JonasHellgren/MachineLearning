package multi_step_temp_diff.environments;

import common.MathUtils;
import common.RandUtils;
import lombok.Getter;
import lombok.ToString;
import multi_step_temp_diff.interfaces_and_abstract.StateInterface;
import multi_step_temp_diff.models.StepReturn;

import java.util.Objects;
import java.util.function.Function;

@Getter
public class ChargeState implements StateInterface<ChargeVariables> {
    public static final double SOC_TOL = 0.01;
    ChargeVariables variables;
    private final int hashCode;

    public static Function<StateInterface<ChargeVariables>,Integer> posA=(s) -> s.getVariables().posA;
    public static Function<StateInterface<ChargeVariables>,Integer> posB=(s) -> s.getVariables().posB;
    public static Function<StateInterface<ChargeVariables>,Double> socA=(s) -> s.getVariables().socA;
    public static Function<StateInterface<ChargeVariables>,Double> socB=(s) -> s.getVariables().socB;
    public static Function<StateInterface<ChargeVariables>,Integer> time=(s) -> s.getVariables().time;


    public ChargeState(ChargeVariables variables) {
        this.variables = variables;
        this.hashCode= Objects.hash(variables.posA,variables.posB,variables.socA,variables.socB,variables.time);
    }

    /*
    public static ChargeState newFromXY(int x, int y) {
        return new ChargeState(ChargeVariables.newFromXY(x,y));
    }

    public static ChargeState newFromRandomPos() {
        int randomX, randomY;
        do {
            randomX = RandUtils.getRandomIntNumber(0, MazeEnvironment.NOF_COLS);
            randomY = RandUtils.getRandomIntNumber(0, MazeEnvironment.NOF_ROWS);
        } while (!MazeEnvironment.isValidStartPosition.apply(randomX,randomY));

        return new ChargeState(ChargeVariables.newFromXY(randomX,randomY));
    }

     */
    @Override
    public StateInterface<ChargeVariables> copy() {
        return new ChargeState(variables.copy());
    }

    @Override
    public void setFromReturn(StepReturn<ChargeVariables> stepReturn) {
        variables.posA=posA.apply(stepReturn.newState);
        variables.posB=posB.apply(stepReturn.newState);
        variables.socA=socA.apply(stepReturn.newState);
        variables.socB=socB.apply(stepReturn.newState);
        variables.time=time.apply(stepReturn.newState);
    }


    @Override
    public boolean equals(Object otherState) {
        //check if the argument is a reference to this object
        if (otherState == this) return true;

        //check if the argument has the correct typ
        if (!(otherState instanceof ChargeState)) return false;

        ChargeState otherCasted = (ChargeState) otherState;
        return Objects.equals(posA.apply(this), posA.apply(otherCasted)) &&
               Objects.equals(posB.apply(this), posB.apply(otherCasted)) &&
               MathUtils.compareDoubleScalars(socA.apply(this), socA.apply(otherCasted), SOC_TOL) &&
               MathUtils.compareDoubleScalars(socB.apply(this), socB.apply(otherCasted), SOC_TOL) &&
               Objects.equals(time.apply(this), time.apply(otherCasted));
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }

    @Override
    public String toString() {
        return variables.toString();
    }

}
