package common;

import common.ListUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static common.MathUtils.accumulatedSum;

/****
 * In the agent method chooseAction() limits as follows are defined. In this example the limits are [0,0.5,0.75,1]
 * they are derived from the action probabilities [0.5,0.25,0.25]
 * If, for example, randomNumberBetweenZeroAndOne is 0.55, then the bucket/action is 1
 * |
 * |_____0_________|__1___|__2___|
 0               0.5    0.75   1

 The method actionProbabilities() gives action probabilities according to soft max, often
 called piTheta in literature

 */

public class BucketLimitsHandler {

    private BucketLimitsHandler() {
    }

    @NotNull
    public static List<Double> getLimits(List<Double> actionProbabilities) {
        List<Double> limits = new ArrayList<>();
        limits.add(0d);
        List<Double> ap = accumulatedSum(actionProbabilities);
        limits.addAll(ap);
        return limits;
    }

    public static void throwIfBadLimits(List<Double> limits) {
        if (ListUtils.findMin(limits).orElseThrow() < 0d || ListUtils.findMin(limits).orElseThrow() > 1) {
            throw new ArithmeticException("Bad action probabilities");
        }
    }

}
