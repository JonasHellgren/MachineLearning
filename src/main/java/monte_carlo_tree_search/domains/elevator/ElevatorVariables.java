package monte_carlo_tree_search.domains.elevator;

import common.MathUtils;
import lombok.Builder;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * states={pos, nPersonsInElevator, nPersonsWaitingFloor1,..,nPersonsWaitingFloor3,SoE}
 */

@Builder
@ToString
public class ElevatorVariables {
    private static final double DELTA = 0.01;
    public static final int DEFAULT_IN_ELEVATOR = 0;
    public static final ArrayList<Integer> EMPTY_LIST = new ArrayList<>();
    public static final double DEFULT_SOE = 1.0;

    public int pos;
    @Builder.Default
    public int nPersonsInElevator= DEFAULT_IN_ELEVATOR;
    @Builder.Default
    public List<Integer> nPersonsWaiting= EMPTY_LIST;
    @Builder.Default
    public double SoE= DEFULT_SOE;


    public ElevatorVariables copy() {
        return ElevatorVariables.builder()
                .pos(pos)
                .nPersonsInElevator(nPersonsInElevator)
                .nPersonsWaiting(new ArrayList<>(nPersonsWaiting))
                .SoE(SoE)
                .build();
    }

    @Override
    public boolean equals(Object obj) {

        //check if the argument is a reference to this object
        if (obj == this) return true;

        //check if the argument has the correct typ
        if (!(obj instanceof ElevatorVariables)) return false;

        //For each significant field in the class, check if that field matches the corresponding field of this object
        ElevatorVariables equalsSample = (ElevatorVariables) obj;
        boolean isSamePos= this.pos == equalsSample.pos;
        boolean isSamePersonsInElevator = this.nPersonsInElevator == equalsSample.nPersonsInElevator;
        boolean isSamePersonsWaiting = this.nPersonsWaiting.equals(equalsSample.nPersonsWaiting);
        boolean isSameSoE= MathUtils.compareDoubleScalars(this.SoE,equalsSample.SoE, DELTA);
        return isSamePos && isSamePersonsInElevator && isSamePersonsWaiting && isSameSoE;
    }


}
