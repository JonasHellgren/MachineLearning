package policy_gradient_problems.twoArmedBandit;

import common.IndexFinder;
import common.ListUtils;
import common.MathUtils;
import common.RandUtils;
import lombok.Builder;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;
import policy_gradient_problems.common.SoftMaxEvaluator;

import java.util.ArrayList;
import java.util.List;

/****
 * In chooseAction() limits as follows are defined. In this example the limits are [0,0.5,0.75,1]
 * they are derived from the action probabilities [0.5,0.25,0.25]
 * If, for example, randomNumberBetweenZeroAndOne is 0.55, then the bucket/action is 1

 * |
 * |_____0_________|__1___|__2___|
   0               0.5    0.75   1
 */

@Builder
public class Agent {

    public static final double THETA0 = 0.5, THETA1 = 0.5;
    public static final int NOF_ACTIONS = 2;
    @Builder.Default
    ArrayRealVector thetaVector =new ArrayRealVector(new double[]{THETA0,THETA1});

    @Builder.Default
    int nofActions = NOF_ACTIONS;

    public static Agent newDefault() {
        return Agent.builder().build();
    }

    public static Agent newWithThetas(double t0, double t1) {
        return Agent.builder().thetaVector(new ArrayRealVector(new double[]{t0,t1})).build();
    }


    public int chooseAction() {
        List<Double> piTheta = piTheta();
        List<Double> limits = getLimits(piTheta);
        throwIfBadLimits(limits);
        double randomNumberBetweenZeroAndOne = RandUtils.getRandomDouble(0, 1);
        return IndexFinder.findBucket(ListUtils.toArray(limits), randomNumberBetweenZeroAndOne);
    }

    public void setTheta(ArrayRealVector theta) {
        this.thetaVector = theta;
    }

    public ArrayRealVector getThetaVector() {
        return thetaVector;
    }

    public List<Double> piTheta() {  //action probabilities according to soft max
      return  SoftMaxEvaluator.getProbabilities(ListUtils.arrayPrimitiveDoublesToList(thetaVector.toArray()));
    }

    public ArrayRealVector getGradLogVector(int action) {
        return new ArrayRealVector(getGradLogArray(action));
    }

    @NotNull
    private static List<Double> getLimits(List<Double> piTheta) {
        List<Double> limits = new ArrayList<>();
        limits.add(0d);
        List<Double> accumProbabilities=MathUtils.accumSum(piTheta);
        limits.addAll(accumProbabilities);
        return limits;
    }

    private static void throwIfBadLimits(List<Double> limits) {
        if (ListUtils.findMin(limits).orElseThrow() < 0d || ListUtils.findMin(limits).orElseThrow() > 1) {
            throw new RuntimeException("Bad action probabilities");
        }
    }


    private double[] getGradLogArray(int action) {
        return (action==0)
                ? new double[]{piTheta().get(1),-piTheta().get(1)}
                : new double[]{-piTheta().get(0),piTheta().get(0)};
    }
}
