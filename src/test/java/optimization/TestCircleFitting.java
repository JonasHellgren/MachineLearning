package optimization;

import optimization.models.CircleScalar;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.SimpleValueChecker;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.MultivariateOptimizer;
import org.apache.commons.math3.optim.nonlinear.scalar.gradient.NonLinearConjugateGradientOptimizer;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

/**
 * Thew objective is to fit circle center to given points on circle diameter
 */

public class TestCircleFitting {


    public static final double REL_TRES_HOLD = 1e-10;
    public static final double ABS_TRES_HOLD = 1e-10;
    public static final double[] INITIAL_GUESS = {98.680, 47.345};
    public static final int NOF_EVAL_MAX = 1000;
    public static final double DELTA = 1.0e-1;

    @Test
    public void testCircleFitting2() {

        //x = circle center = (px,py)
        CircleScalar circle = new CircleScalar();
        circle.addPoint(30.0, 68.0);
        circle.addPoint(50.0, -6.0);
        circle.addPoint(110.0, -20.0);
        circle.addPoint(35.0, 15.0);
        circle.addPoint(45.0, 97.0);
        MultivariateOptimizer optimizer=TestHelper.getConjugateGradientOptimizer(REL_TRES_HOLD, ABS_TRES_HOLD);
        PointValuePair optimum = TestHelper.gradientOptimize(
                optimizer,circle.getObjectiveFunction(),circle.getObjectiveFunctionGradient(),
                INITIAL_GUESS, NOF_EVAL_MAX);

        TestHelper.printPointValuePair(optimum);
        TestHelper.printOptimizerStats(optimizer);

        Vector2D center = new Vector2D(optimum.getPointRef()[0], optimum.getPointRef()[1]);
        Assert.assertEquals(69.960161753, circle.getRadius(center), DELTA);
        Assert.assertEquals(96.075902096, center.getX(), DELTA);
        Assert.assertEquals(48.135167894, center.getY(), DELTA);
    }

}
