package optimization;

import common.CpuTimer;
import optimization.models.SumOfThree;
import org.apache.commons.math3.optim.*;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.MultivariateOptimizer;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunctionGradient;
import org.apache.commons.math3.optim.nonlinear.scalar.gradient.NonLinearConjugateGradientOptimizer;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;

/**
 *   larger PEN_COEFF requires smaller EPS
 *   Small EPS => more accurate solution but more iterations/function calls
 */

public class TestSumOfThree {

    public static final double DELTA = 1.0e-1;
    public static final int NOF_EVAL_MAX = 10_000;
    public static final double EPS = 1e-3;
    public static final double PEN_COEFF = 1e1;
    public static final double RELATIVE_THRESHOLD = EPS;
    public static final double ABSOLUTE_THRESHOLD = EPS;
    public static final double[] OPT_POINT = {0, 0, 1.0};

    double[] initialGuess = {0.5,0.5,0.5};


    @Test
    public void givenPoint001_thenMinuesTwoObj() {
        SumOfThree sumOfThree=new SumOfThree(PEN_COEFF);
        double value = sumOfThree.getObjectiveFunction().getObjectiveFunction().value(new double[]{0, 0, 1});
        System.out.println("value(new double[] {0,0,1}) = " + value);

        Assert.assertEquals(-2,value,DELTA);
    }

    @Test
    public void givenNonCorrectGradient_thenWrongOptimum() {
        MultivariateOptimizer optimizer = getMultivariateOptimizer();
        SumOfThree sumOfThree=new SumOfThree(PEN_COEFF);
        PointValuePair optimum = gradientOptimize(optimizer, sumOfThree.getObjectiveFunction(), sumOfThree.getWrongGradient());
        printFoundPoint(optimum);
        printOptimizerStats(optimizer);
        Assert.assertFalse(Arrays.equals(OPT_POINT, optimum.getPointRef()));
    }


    @Test
    public void givenCorrectGradient_thenCorrectOptimum() {
        MultivariateOptimizer optimizer = getMultivariateOptimizer();
        SumOfThree sumOfThree=new SumOfThree(PEN_COEFF);
        PointValuePair optimum = null;
        CpuTimer timer=new CpuTimer();
        int nofCalls = 1000;
        for (int i = 0; i < nofCalls; i++) {
            optimum = gradientOptimize(optimizer, sumOfThree.getObjectiveFunction(), sumOfThree.getFiniteDiffGradient(EPS));
        }

        System.out.println("time per optimize (ms) = " + (double) timer.absoluteProgress()/(double) nofCalls);
        printFoundPoint(optimum);
        printOptimizerStats(optimizer);
        Assert.assertArrayEquals(OPT_POINT, optimum.getPointRef(), DELTA);
    }

    private PointValuePair gradientOptimize(MultivariateOptimizer optimizer,
                                            ObjectiveFunction objFunction,
                                            ObjectiveFunctionGradient gradientFunction) {
        return optimizer.optimize(new MaxEval(NOF_EVAL_MAX),
                objFunction, gradientFunction,
                GoalType.MINIMIZE,
                new InitialGuess(initialGuess));
    }


    @Test
    @Ignore("Bound does not work")
    public void givenLineFiniteDiffGradientBounds_thenZeroIsOptimum() {
        MultivariateOptimizer optimizer = getMultivariateOptimizer();
        SumOfThree sumOfThree=new SumOfThree(PEN_COEFF);
        double[] lowerBounds = {-1.0, -1.0,-1.0};
        double[] upperBounds = {10.0, 10.0,10.0};
        PointValuePair optimum =
                optimizer.optimize(new MaxEval(NOF_EVAL_MAX),
                        sumOfThree.getObjectiveFunction(),
                        sumOfThree.getFiniteDiffGradient(EPS),
                        GoalType.MINIMIZE,
                        new SimpleBounds(lowerBounds, upperBounds),  //gives MathUnsupportedOperationException
                        new InitialGuess(initialGuess));
        printFoundPoint(optimum);
        printOptimizerStats(optimizer);

    }

    private static void printOptimizerStats(MultivariateOptimizer optimizer) {
        System.out.println("optimizer.getEvaluations() = " + optimizer.getEvaluations());
        System.out.println("optimizer.getIterations() = " + optimizer.getIterations());
    }

    @NotNull
    private static MultivariateOptimizer getMultivariateOptimizer() {
        return new NonLinearConjugateGradientOptimizer(
                NonLinearConjugateGradientOptimizer.Formula.POLAK_RIBIERE,
                new SimpleValueChecker(RELATIVE_THRESHOLD, ABSOLUTE_THRESHOLD));
    }

    private static void printFoundPoint(PointValuePair optimum) {
        System.out.println("optimum.getPointRef() = " + Arrays.toString(optimum.getPointRef()));
    }

}
