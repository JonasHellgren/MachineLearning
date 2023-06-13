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
        SumOfThree sumOfThree=new SumOfThree(PEN_COEFF,EPS);
        double value = sumOfThree.getObjectiveFunction().getObjectiveFunction().value(new double[]{0, 0, 1});
        System.out.println("value(new double[] {0,0,1}) = " + value);

        Assert.assertEquals(-2,value,DELTA);
    }

    @Test
    public void givenNonCorrectGradient_thenWrongOptimum() {
        SumOfThree sumOfThree=new SumOfThree(PEN_COEFF,EPS);
        MultivariateOptimizer optimizer =
                TestHelper.getConjugateGradientOptimizer(RELATIVE_THRESHOLD,ABSOLUTE_THRESHOLD);
        PointValuePair optimum = TestHelper.gradientOptimize(
                optimizer,sumOfThree.getObjectiveFunction(),sumOfThree.getWrongGradient(),
                initialGuess, NOF_EVAL_MAX);

        TestHelper.printPointValuePair(optimum);
        TestHelper.printOptimizerStats(optimizer);
        Assert.assertFalse(Arrays.equals(OPT_POINT, optimum.getPointRef()));
    }


    @Test
    public void givenCorrectGradient_thenCorrectOptimum() {
        MultivariateOptimizer optimizer =
                TestHelper.getConjugateGradientOptimizer(RELATIVE_THRESHOLD,ABSOLUTE_THRESHOLD);
        SumOfThree sumOfThree=new SumOfThree(PEN_COEFF,EPS);
        PointValuePair optimum = null;
        CpuTimer timer=new CpuTimer();
        int nofCalls = 1000;
        for (int i = 0; i < nofCalls; i++) {
            optimum = TestHelper.gradientOptimize(
                    optimizer,sumOfThree.getObjectiveFunction(),sumOfThree.getFiniteDiffGradient(),
                    initialGuess, NOF_EVAL_MAX);
        }

        System.out.println("time per optimize (ms) = " + (double) timer.absoluteProgress()/(double) nofCalls);
        TestHelper.printPointValuePair(optimum);

        TestHelper.printOptimizerStats(optimizer);
        Assert.assertArrayEquals(OPT_POINT, optimum.getPointRef(), DELTA);
    }


    @Test
    @Ignore("Bound does not work")
    public void givenLineFiniteDiffGradientBounds_thenZeroIsOptimum() {
        MultivariateOptimizer optimizer =
                TestHelper.getConjugateGradientOptimizer(RELATIVE_THRESHOLD,ABSOLUTE_THRESHOLD);
        SumOfThree sumOfThree=new SumOfThree(PEN_COEFF,EPS);
        double[] lowerBounds = {-1.0, -1.0,-1.0};
        double[] upperBounds = {10.0, 10.0,10.0};
        PointValuePair optimum =
                optimizer.optimize(new MaxEval(NOF_EVAL_MAX),
                        sumOfThree.getObjectiveFunction(),
                        sumOfThree.getFiniteDiffGradient(),
                        GoalType.MINIMIZE,
                        new SimpleBounds(lowerBounds, upperBounds),  //gives MathUnsupportedOperationException
                        new InitialGuess(initialGuess));
        TestHelper.printPointValuePair(optimum);
        TestHelper.printOptimizerStats(optimizer);

    }


}
