package policy_gradient_problems.twoArmedBandit;

import common.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunctionGradient;
import org.jetbrains.annotations.NotNull;
import policy_gradient_problems.common.SoftMaxEvaluator;

import java.util.ArrayList;
import java.util.List;

import static common.MathUtils.accumulatedSum;

/****
 * In chooseAction() limits as follows are defined. In this example the limits are [0,0.5,0.75,1]
 * they are derived from the action probabilities [0.5,0.25,0.25]
 * If, for example, randomNumberBetweenZeroAndOne is 0.55, then the bucket/action is 1
 * |
 * |_____0_________|__1___|__2___|
 0               0.5    0.75   1

 The method actionProbabilities() gives action probabilities according to soft max, called piTheta in literature

 https://towardsdatascience.com/derivative-of-the-softmax-function-and-the-categorical-cross-entropy-loss-ffceefc081d1

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
        List<Double> actionProbabilities = actionProbabilities(thetaVector.toArray());
        List<Double> limits = getLimits(actionProbabilities);
        throwIfBadLimits(limits);
        double randomNumberBetweenZeroAndOne = RandUtils.getRandomDouble(0, 1);
        return IndexFinder.findBucket(ListUtils.toArray(limits), randomNumberBetweenZeroAndOne);
    }


    public List<Double> actionProbabilities(double[] thetaArr) {
        return SoftMaxEvaluator.getProbabilities(ListUtils.arrayPrimitiveDoublesToList(thetaArr));
    }

    public ArrayRealVector gradLogVector(int action) {
        return new ArrayRealVector(getGradLogArray(action));
    }

    @NotNull
    private static List<Double> getLimits(List<Double> actionProbabilities) {
        List<Double> limits = new ArrayList<>();
        limits.add(0d);
        List<Double> ap = accumulatedSum(actionProbabilities);
        limits.addAll(ap);
        return limits;
    }

    private static void throwIfBadLimits(List<Double> limits) {
        if (ListUtils.findMin(limits).orElseThrow() < 0d || ListUtils.findMin(limits).orElseThrow() > 1) {
            throw new RuntimeException("Bad action probabilities");
        }
    }


    private double[] getGradLogArray(int action) {
        List<Double> ap = actionProbabilities(thetaVector.toArray());
        return (action == 0)
                ? new double[]{ap.get(1), -ap.get(1)}
                : new double[]{-ap.get(0), ap.get(0)};
    }

    private double[] getGradLogArrayFiniteDiff(int action) {
        double[] gradLog = new double[nofActions];
        for (int a = 0; a < nofActions; a++) {
            int finalA = a;
            MultivariateFunction mf = theta -> {
                double[] point=thetaVector.toArray();
                point[finalA]=theta[0];
                return actionProbabilitiesArr(point)[finalA];
            };
            ObjectiveFunction of = new ObjectiveFunction(mf);
            FiniteDiffGradientCalculator diffGradientCalculator = new FiniteDiffGradientCalculator(of, 1e-5);
            ObjectiveFunctionGradient gradient = diffGradientCalculator.getFiniteDiffGradient();
            double[] arrIn=new double[]{thetaVector.toArray()[finalA]};
            double[] gradientArr = gradient.getObjectiveFunctionGradient().value(arrIn);
            gradLog[finalA] = gradientArr[finalA];
        }
        return gradLog;
    }

    public double[] actionProbabilitiesArr(double[] theta) {
        return SoftMaxEvaluator.getProbabilities(theta);
    }

    private double[] getGradLogArrayFiniteDiff0(int action) {
        double[] gradLog = new double[nofActions];
        for (int a = 0; a < nofActions; a++) {
            int finalA = a;
            MultivariateFunction mf = point -> actionProbability0(finalA);
            ObjectiveFunction of = new ObjectiveFunction(mf);
            FiniteDiffGradientCalculator diffGradientCalculator = new FiniteDiffGradientCalculator(of, 1e-5);
            ObjectiveFunctionGradient gradient = diffGradientCalculator.getFiniteDiffGradient();
            double[] gradientArr = gradient.getObjectiveFunctionGradient().value(thetaVector.toArray());
            gradLog[a] = gradientArr[0];
        }
        return gradLog;
    }

    public double actionProbability0(int thetaIndex) {
        double[] probArr = SoftMaxEvaluator.getProbabilities(thetaVector.toArray());
        return probArr[thetaIndex];
    }

}
