package optimization;

import common.RandUtils;
import optimization.models.ConstrainedRosenbrock;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.SimpleValueChecker;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.MultivariateOptimizer;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunctionGradient;
import org.apache.commons.math3.optim.nonlinear.scalar.gradient.NonLinearConjugateGradientOptimizer;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

/**
 * ConstrainedRosenbrock has multiple local minima
 */

import java.util.Arrays;

public class TestConstrainedRosenbrock {
    public static final double DELTA = 1.0e-1;
    public static final int NOF_EVAL_MAX = 10_000;
    public static final double EPS = 1e-3;
    public static final double PEN_COEFF = 1e1;
    public static final double RELATIVE_THRESHOLD = EPS;
    public static final double ABSOLUTE_THRESHOLD = EPS;
    public static final double[] OPT_POINT = {1.0, 1.0};
    public static final double[] INITIAL_GUESS = {0.5, 0.5};


    @Test
    public void givenOneInitGuess_thenNonCorrectOptimum() {
        MultivariateOptimizer optimizer = getMultivariateOptimizer();
        ConstrainedRosenbrock sumOfThree = new ConstrainedRosenbrock(PEN_COEFF);
        double[] initialGuess = INITIAL_GUESS;

        PointValuePair optimum = gradientOptimize(
                optimizer,
                sumOfThree.getObjectiveFunction(),
                sumOfThree.getFiniteDiffGradient(EPS),
                initialGuess);

        printFoundPoint(optimum);
        printOptimizerStats(optimizer);
        Assert.assertFalse(Arrays.equals(OPT_POINT, optimum.getPointRef()));
    }

    @Test
    public void givenManyInitGuesses_thenCorrectOptimum() {
        MultivariateOptimizer optimizer = getMultivariateOptimizer();
        ConstrainedRosenbrock sumOfThree = new ConstrainedRosenbrock(PEN_COEFF);
        PointValuePair bestOptimum = new PointValuePair(INITIAL_GUESS,Double.MAX_VALUE);

        for (int i = 0; i <100 ; i++) {
            PointValuePair optimum = getResults(optimizer, sumOfThree);
            bestOptimum = calcOptimum(bestOptimum, optimum);
        }

        printFoundPoint(bestOptimum);
        printOptimizerStats(optimizer);
        Assert.assertArrayEquals(OPT_POINT, bestOptimum.getPointRef(), DELTA);
    }

    private static PointValuePair calcOptimum(PointValuePair bestOptimum, PointValuePair optimum) {
        if (optimum.getValue()< bestOptimum.getValue()) {
            bestOptimum = optimum;
            System.out.println("New best found");
            printFoundPoint(bestOptimum);
        }
        return bestOptimum;  //keep old if new is not better
    }

    private PointValuePair getResults(MultivariateOptimizer optimizer, ConstrainedRosenbrock sumOfThree) {
        double[] initialGuess = new double[]{getSingleRandom(), getSingleRandom()};
        return gradientOptimize(
                optimizer,
                sumOfThree.getObjectiveFunction(),
                sumOfThree.getFiniteDiffGradient(EPS),
                initialGuess);
    }

    private static double getSingleRandom() {
        return RandUtils.calcRandomFromInterval(ConstrainedRosenbrock.LB, ConstrainedRosenbrock.UB);
    }

    private PointValuePair gradientOptimize(MultivariateOptimizer optimizer,
                                            ObjectiveFunction objFunction,
                                            ObjectiveFunctionGradient gradientFunction,
                                            double[] initialGuess) {
        return optimizer.optimize(new MaxEval(NOF_EVAL_MAX),
                objFunction, gradientFunction,
                GoalType.MINIMIZE,
                new InitialGuess(initialGuess));
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
