package optimization;

import common.RandUtils;
import optimization.models.ConstrainedRosenbrock;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.nonlinear.scalar.MultivariateOptimizer;
import org.junit.Assert;
import org.junit.Test;
import java.util.Arrays;


/**
 * ConstrainedRosenbrock has multiple local minima
 */
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
        ConstrainedRosenbrock sumOfThree = new ConstrainedRosenbrock(PEN_COEFF,EPS);
        MultivariateOptimizer optimizer = TestHelper.getConjugateGradientOptimizer(RELATIVE_THRESHOLD,ABSOLUTE_THRESHOLD);
        PointValuePair optimum = TestHelper.gradientOptimize(optimizer,
                sumOfThree.getObjectiveFunction(),sumOfThree.getFiniteDiffGradient(),INITIAL_GUESS, NOF_EVAL_MAX);

        TestHelper.printPointValuePair(optimum);
        TestHelper.printOptimizerStats(optimizer);
        Assert.assertFalse(Arrays.equals(OPT_POINT, optimum.getPointRef()));
    }

    @Test
    public void givenManyInitGuesses_thenCorrectOptimum() {
        ConstrainedRosenbrock sumOfThree = new ConstrainedRosenbrock(PEN_COEFF,EPS);
        MultivariateOptimizer optimizer = TestHelper.getConjugateGradientOptimizer(RELATIVE_THRESHOLD,ABSOLUTE_THRESHOLD);
        PointValuePair bestOptimum = new PointValuePair(INITIAL_GUESS,Double.MAX_VALUE);

        for (int i = 0; i <100 ; i++) {
            PointValuePair optimum = getResultsFromRandomInitPoint(optimizer, sumOfThree);
            bestOptimum = calcOptimum(bestOptimum, optimum);
        }

        TestHelper.printPointValuePair(bestOptimum);
        TestHelper.printOptimizerStats(optimizer);
        Assert.assertArrayEquals(OPT_POINT, bestOptimum.getPointRef(), DELTA);
    }

    private static PointValuePair calcOptimum(PointValuePair bestOptimum, PointValuePair optimum) {
        if (optimum.getValue()< bestOptimum.getValue()) {
            bestOptimum = optimum;
            System.out.println("New best found");
            TestHelper.printPointValuePair(bestOptimum);
        }
        return bestOptimum;  //keep old if new is not better
    }

    private PointValuePair getResultsFromRandomInitPoint(MultivariateOptimizer optimizer, ConstrainedRosenbrock model) {
        double[] initialGuess = new double[]{getSingleRandom(), getSingleRandom()};
        return TestHelper.gradientOptimize(optimizer,
                model.getObjectiveFunction(),model.getFiniteDiffGradient(),initialGuess, NOF_EVAL_MAX);
    }

    private static double getSingleRandom() {
        return RandUtils.calcRandomFromInterval(ConstrainedRosenbrock.LB, ConstrainedRosenbrock.UB);
    }

}
