package optimization;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.NelderMeadSimplex;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.SimplexOptimizer;
import org.junit.Assert;
import org.junit.Test;

/**
 * Gradient free
 *
 * In this example, we define the objective function using the MultivariateFunction interface. The gradient is not
 * explicitly provided. The SimplexOptimizer will approximate the gradient using numerical finite
 * differences.
 *
 */

public class TestBowlOptimizationNelderMeadSimplex {
    public static final double DELTA = 1.0e-1;
    public static final double ABS_TOL = 1e-3;
    public static final double REL_TOL = 0.1;
    @Test
    public void givenBowl_thenOrigoIsOptimim () {
        // Define the objective function
        ObjectiveFunction objective = new ObjectiveFunction(x -> {
            double a = x[0];
            double b = x[1];

            if (a < 0 || a > 1 || b < 0 || b > 2) {
                return Double.POSITIVE_INFINITY; // Outside bounds
            }

            return a * a + b * b; // Example: Minimize a^2 + b^2
        });

        double[] initialGuess = {1.0, 1.0};
        double simplexSize = 0.1;
        SimplexOptimizer optimizer = new SimplexOptimizer(REL_TOL, ABS_TOL);

        // Optimize the objective function
        PointValuePair result = optimizer.optimize(
                new MaxEval(1000),                    // Maximum number of evaluations
                objective,                           // Objective function
                GoalType.MINIMIZE,                   // Minimization problem
                new NelderMeadSimplex(initialGuess.length, simplexSize),   // Nelder-Mead simplex
                new InitialGuess(initialGuess));

        Vector2D point = new Vector2D(result.getPointRef()[0], result.getPointRef()[1]);
        System.out.println("point = " + point);
        Assert.assertEquals(0, point.getX(), DELTA);
        Assert.assertEquals(0, point.getY(), DELTA);
    }
}
