package optimization;


import optimization.models.Bowl;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.nonlinear.scalar.MultivariateOptimizer;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class TestBowlOptimizationNonLinearConjugateGradient {

    public static final double DELTA = 1.0e-1;
    public static final double EPSILON = 1e-10;
    public static final int NOF_EVAL_MAX = 100;
    public static final double RELATIVE_THRESHOLD = 1e-1;
    public static final double ABSOLUTE_THRESHOLD = 1e-2;
    double[] INITIAL_GUESS = {1.0, 1.0};

    @Test
    public void givenBowl_thenOrigoIsOptimim () {
        Bowl bowl=new Bowl();
        MultivariateOptimizer optimizer=TestHelper.getConjugateGradientOptimizer(RELATIVE_THRESHOLD, ABSOLUTE_THRESHOLD);
        PointValuePair optimum = TestHelper.gradientOptimize(
                optimizer,bowl.getObjectiveFunction(),bowl.getGradient(),
                INITIAL_GUESS, NOF_EVAL_MAX);

        TestHelper.printPointValuePair(optimum);
        doAsserts(optimum);
    }

    @Test
    public void givenBowlFinitDiffGradient_thenOrigoIsOptimim () {
        Bowl bowl=new Bowl();
        MultivariateOptimizer optimizer=TestHelper.getConjugateGradientOptimizer(RELATIVE_THRESHOLD, ABSOLUTE_THRESHOLD);
        PointValuePair optimum = TestHelper.gradientOptimize(
                optimizer,bowl.getObjectiveFunction(),bowl.getFiniteDiffGradient(EPSILON),
                INITIAL_GUESS, NOF_EVAL_MAX);

        TestHelper.printPointValuePair(optimum);
        doAsserts(optimum);
    }

    private static void doAsserts(PointValuePair optimum) {
        Vector2D point = new Vector2D(optimum.getPointRef()[0], optimum.getPointRef()[1]);
        Assert.assertEquals(0, point.getX(), DELTA);
        Assert.assertEquals(0, point.getY(), DELTA);
    }

}
