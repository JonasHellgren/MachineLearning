package policy_gradient_problems.the_problems.twoArmedBandit;

import common.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.linear.ArrayRealVector;
import policy_gradient_problems.abstract_classes.AgentInterface;

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
public class AgentBanditRealVector implements AgentInterface {  //todo AgentThetaPolicyInterface

    public static final double THETA0 = 0.5, THETA1 = 0.5;
    public static final ArrayRealVector ARRAY_REAL_VECTOR =
            new ArrayRealVector(new double[]{THETA0, THETA1});
    @Builder.Default
    ArrayRealVector thetaVector = ARRAY_REAL_VECTOR;
    @Builder.Default
    int nofActions = ARRAY_REAL_VECTOR.getDimension();

    public static AgentBanditRealVector newDefault() {
        return AgentBanditRealVector.builder().build();
    }

    public static AgentBanditRealVector newWithThetas(double t0, double t1) {
        return AgentBanditRealVector.builder().thetaVector(new ArrayRealVector(new double[]{t0, t1})).build();
    }

    @Override
    public int chooseActionOld() {
        var limits = getLimits(getActionProbabilities());
        throwIfBadLimits(limits);
        return findBucket(ListUtils.toArray(limits), RandUtils.randomNumberBetweenZeroAndOne());
    }

    public List<Double> actionProbabilities() {
        return actionProbabilities(thetaVector.toArray());
    }

    public ArrayRealVector calcGradLogVector(int action) {
        return new ArrayRealVector(calculateGradLog(action, getActionProbabilities()));
    }

    private List<Double> getActionProbabilities() {
        return actionProbabilities(thetaVector.toArray());
    }

    private List<Double> actionProbabilities(double[] thetaArr) {
        return getProbabilities(ListUtils.arrayPrimitiveDoublesToList(thetaArr));
    }
}
