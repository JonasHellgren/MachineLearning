package monte_carlo_tree_search.domains.elevator;

import common.MathUtils;
import lombok.Builder;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * states={pos, nPersonsInElevator, nPersonsWaitingFloor1,..,nPersonsWaitingFloor3,SoE}
 */

@Builder
@ToString
public class VariablesElevator {
    private static final double DELTA = 0.01;
    public static final int DEFAULT_SPEED = 0;
    public static final int DEFAULT_POS = 0;
    public static final int DEFAULT_IN_ELEVATOR = 0;
    public static final List<Integer> EMPTY_LIST = Arrays.asList(0,0,0);
    public static final double DEFULT_SOE = 1.0;

    @Builder.Default
    public int speed=DEFAULT_SPEED;
    @Builder.Default
    public int pos=DEFAULT_POS;
    @Builder.Default
    public int nPersonsInElevator= DEFAULT_IN_ELEVATOR;
    @Builder.Default
    public List<Integer> nPersonsWaiting= EMPTY_LIST;
    @Builder.Default
    public double SoE= DEFULT_SOE;


    public VariablesElevator copy() {
        return VariablesElevator.builder()
                .speed(speed)
                .pos(pos)
                .nPersonsInElevator(nPersonsInElevator)
                .nPersonsWaiting(new ArrayList<>(nPersonsWaiting))
                .SoE(SoE)
                .build();
    }

    public int nofWaiting() {
        return nPersonsWaiting.stream().mapToInt(Integer::intValue).sum();
    }

    @Override
    public boolean equals(Object obj) {

        //check if the argument is a reference to this object
        if (obj == this) return true;

        //check if the argument has the correct typ
        if (!(obj instanceof VariablesElevator)) return false;

        //For each significant field in the class, check if that field matches the corresponding field of this object
        VariablesElevator equalsSample = (VariablesElevator) obj;
        boolean isSamePos= this.pos == equalsSample.pos;
        boolean isSamePersonsInElevator = this.nPersonsInElevator == equalsSample.nPersonsInElevator;
        boolean isSamePersonsWaiting = this.nPersonsWaiting.equals(equalsSample.nPersonsWaiting);
        boolean isSameSoE= MathUtils.compareDoubleScalars(this.SoE,equalsSample.SoE, DELTA);
        return isSamePos && isSamePersonsInElevator && isSamePersonsWaiting && isSameSoE;
    }


}
