package monte_carlo_tree_search.domains.elevator;

import common.ListUtils;
import common.MathUtils;
import common.RandUtils;
import lombok.Builder;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * states={posReal, nPersonsInElevator, nPersonsWaitingFloor1,..,nPersonsWaitingFloor3,SoE}
 */

@Builder
@ToString
public class VariablesElevator {
    private static final double DELTA = 0.01;
    public static final int DEFAULT_SPEED = 0;
    public static final int DEFAULT_POS = 0;
    public static final int DEFAULT_IN_ELEVATOR = 0;
    public static final List<Integer> EMPTY_LIST = Arrays.asList(0, 0, 0);
    public static final double DEFAULT_SOE = 1.0;
    private static final int SPEED_STILL = ActionElevator.STILL_ACTION;
    private static final int SPEED_UP = ActionElevator.MAX_ACTION_DEFAULT;
    private static final int SPEED_DOWN = ActionElevator.MIN_ACTION_DEFAULT;

    @Builder.Default
    public int speed = DEFAULT_SPEED;
    @Builder.Default
    public int pos = DEFAULT_POS;
    @Builder.Default
    public int nPersonsInElevator = DEFAULT_IN_ELEVATOR;
    @Builder.Default
    public List<Integer> nPersonsWaiting = EMPTY_LIST;
    @Builder.Default
    public double SoE = DEFAULT_SOE;

    public static VariablesElevator newRandom(int maxNPersonsInElevator, int maxNPersonsWaitingTotal) {

        RandUtils<Integer> speedRand = new RandUtils<>();
        List<Integer> waitingList = createWaitingList(maxNPersonsWaitingTotal);

        return VariablesElevator.builder()
                .speed(speedRand.getRandomItemFromList(Arrays.asList(SPEED_UP, SPEED_STILL, SPEED_DOWN)))
                .pos(RandUtils.getRandomIntNumber(EnvironmentElevator.MIN_POS, EnvironmentElevator.MAX_POS + 1))
                .nPersonsInElevator(RandUtils.getRandomIntNumber(0, maxNPersonsInElevator + 1))
                .nPersonsWaiting(waitingList)
                .SoE(RandUtils.getRandomDouble(EnvironmentElevator.SOE_LOW, EnvironmentElevator.SoE_HIGH))
                .build();
    }

    private static List<Integer> createWaitingList(int maxNPersonsWaitingTotal) {
        List<Integer> waitingList;
        do {
            int nw1 = RandUtils.getRandomIntNumber(0, maxNPersonsWaitingTotal + 1);
            int nw2 = RandUtils.getRandomIntNumber(0, maxNPersonsWaitingTotal + 1);
            int nw3 = RandUtils.getRandomIntNumber(0, maxNPersonsWaitingTotal + 1);
            waitingList = Arrays.asList(nw1, nw2, nw3);
        } while (ListUtils.sumIntegerList(waitingList) != maxNPersonsWaitingTotal);
        return waitingList;
    }

    public static VariablesElevator newDefault() {
        return VariablesElevator.builder().build();
    }


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
        boolean isSamePos = this.pos == equalsSample.pos;
        boolean isSamePersonsInElevator = this.nPersonsInElevator == equalsSample.nPersonsInElevator;
        boolean isSamePersonsWaiting = this.nPersonsWaiting.equals(equalsSample.nPersonsWaiting);
        boolean isSameSoE = MathUtils.isEqualDoubles(this.SoE, equalsSample.SoE, DELTA);
        return isSamePos && isSamePersonsInElevator && isSamePersonsWaiting && isSameSoE;
    }


}
