package gradient_optimization;

import gradient_optimization.one_dim.CircleScalar;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.SimpleValueChecker;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.GradientMultivariateOptimizer;
import org.apache.commons.math3.optim.nonlinear.scalar.MultiStartMultivariateOptimizer;
import org.apache.commons.math3.optim.nonlinear.scalar.MultivariateOptimizer;
import org.apache.commons.math3.optim.nonlinear.scalar.gradient.NonLinearConjugateGradientOptimizer;
import org.apache.commons.math3.random.GaussianRandomGenerator;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomVectorGenerator;
import org.apache.commons.math3.random.UncorrelatedRandomVectorGenerator;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

/**
 * Thew objective is to fit circle center to given points on circle diameter
 */

public class TestCircleFitting {


    @Test
    public void testCircleFitting2() {

        //x = circle center = (px,py)
        CircleScalar circle = new CircleScalar();
        circle.addPoint(30.0, 68.0);
        circle.addPoint(50.0, -6.0);
        circle.addPoint(110.0, -20.0);
        circle.addPoint(35.0, 15.0);
        circle.addPoint(45.0, 97.0);
        MultivariateOptimizer optimizer = new NonLinearConjugateGradientOptimizer(
                NonLinearConjugateGradientOptimizer.Formula.POLAK_RIBIERE,
                new SimpleValueChecker(1e-10, 1e-10));
        PointValuePair optimum =
                optimizer.optimize(new MaxEval(1000),
                        circle.getObjectiveFunction(),
                        circle.getObjectiveFunctionGradient(),
                        GoalType.MINIMIZE,
                        new InitialGuess(new double[]{98.680, 47.345}));
        System.out.println("optimum = " + Arrays.toString(optimum.getPoint()));


        Vector2D center = new Vector2D(optimum.getPointRef()[0], optimum.getPointRef()[1]);
        Assert.assertEquals(69.960161753, circle.getRadius(center), 1.0e-1);
        Assert.assertEquals(96.075902096, center.getX(), 1.0e-1);
        Assert.assertEquals(48.135167894, center.getY(), 1.0e-1);
    }

}
