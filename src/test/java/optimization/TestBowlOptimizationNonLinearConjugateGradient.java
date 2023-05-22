package optimization;


import optimization.models.Bowl;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.SimpleValueChecker;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.MultivariateOptimizer;
import org.apache.commons.math3.optim.nonlinear.scalar.gradient.NonLinearConjugateGradientOptimizer;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class TestBowlOptimizationNonLinearConjugateGradient {

    public static final double DELTA = 1.0e-1;
    public static final double EPSILON = 1e-10;
    public static final int NOF_EVAL_MAX = 100;
    public static final double RELATIVE_THRESHOLD = 1e-1;
    public static final double ABSOLUTE_THRESHOLD = 1e-2;
    double[] initialGuess = {1.0, 1.0};

    @Test
    public void givenBowl_thenOrigoIsOptimim () {
        MultivariateOptimizer optimizer = getMultivariateOptimizer();
        Bowl bowl=new Bowl();
        PointValuePair optimum =
                optimizer.optimize(new MaxEval(NOF_EVAL_MAX),
                        bowl.getObjectiveFunction(),
                        bowl.getGradient(),
                        GoalType.MINIMIZE,
                        new InitialGuess(initialGuess));
        printAndAssert(optimum);
    }

    @Test
    public void givenBowlFinitDiffGradient_thenOrigoIsOptimim () {
        MultivariateOptimizer optimizer = getMultivariateOptimizer();
        Bowl bowl=new Bowl();
        PointValuePair optimum =
                optimizer.optimize(new MaxEval(NOF_EVAL_MAX),
                        bowl.getObjectiveFunction(),
                        bowl.getFiniteDiffGradient(EPSILON),
                        GoalType.MINIMIZE,
                        new InitialGuess(initialGuess));
        printAndAssert(optimum);
    }

    private static void printAndAssert(PointValuePair optimum) {
        System.out.println("optimum.getPointRef() = " + Arrays.toString(optimum.getPointRef()));
        Vector2D point = new Vector2D(optimum.getPointRef()[0], optimum.getPointRef()[1]);
        Assert.assertEquals(0, point.getX(), DELTA);
        Assert.assertEquals(0, point.getY(), DELTA);
    }

    @NotNull
    private static MultivariateOptimizer getMultivariateOptimizer() {
        return new NonLinearConjugateGradientOptimizer(
                NonLinearConjugateGradientOptimizer.Formula.POLAK_RIBIERE,
                new SimpleValueChecker(RELATIVE_THRESHOLD, ABSOLUTE_THRESHOLD));
    }
}
