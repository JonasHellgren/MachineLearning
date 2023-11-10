package policy_gradient_problems.twoArmedBandit;

import common.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.linear.ArrayRealVector;
import java.util.List;
import static common.IndexFinder.findBucket;
import static policy_gradient_problems.common.BucketLimitsHandler.*;
import static policy_gradient_problems.common.GradLogCalculator.calculateGradLog;
import static policy_gradient_problems.common.SoftMaxEvaluator.getProbabilities;

/***
 * Bucket is defined in class BucketLimitsHandler
 */

@Builder
@Getter
@Setter
public class Agent {

    public static final double THETA0 = 0.5, THETA1 = 0.5;
    public static final int NOF_ACTIONS = 2;
    @Builder.Default
    ArrayRealVector thetaVector = new ArrayRealVector(new double[]{THETA0, THETA1});
    @Builder.Default
    int nofActions = NOF_ACTIONS;

    public static Agent newDefault() {
        return Agent.builder().build();
    }

    public static Agent newWithThetas(double t0, double t1) {
        return Agent.builder().thetaVector(new ArrayRealVector(new double[]{t0, t1})).build();
    }

    public int chooseAction() {
        var actionProbabilities = actionProbabilities(thetaVector.toArray());
        var limits = getLimits(actionProbabilities);
        throwIfBadLimits(limits);
        return findBucket(ListUtils.toArray(limits), RandUtils.randomNumberBetweenZeroAndOne());
    }

    public List<Double> actionProbabilities(double[] thetaArr) {
        return getProbabilities(ListUtils.arrayPrimitiveDoublesToList(thetaArr));
    }

    public ArrayRealVector gradLogVector(int action) {
        var ap = actionProbabilities(thetaVector.toArray());
        return new ArrayRealVector(calculateGradLog(action,ap));
    }

}
