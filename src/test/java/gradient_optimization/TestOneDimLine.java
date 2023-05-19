package gradient_optimization;

import gradient_optimization.one_dim.Bowl;
import gradient_optimization.one_dim.OneDimLine;
import gradient_optimization.one_dim.ObjectiveFunctionOneDim;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.SimpleValueChecker;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.MultivariateOptimizer;
import org.apache.commons.math3.optim.nonlinear.scalar.gradient.NonLinearConjugateGradientOptimizer;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.Arrays;

public class TestOneDimLine {

    public static final double DELTA = 1.0e-1;
    public static final double EPSILON = 1e-10;
    public static final int NOF_EVAL_MAX = 1000;
    public static final double RELATIVE_THRESHOLD = 1e-1;
    public static final double ABSOLUTE_THRESHOLD = 1e-2;
    double[] initialGuess = {1.0};

    @Test
    public void givenBowl_thenOrigoIsOptimim () {
        MultivariateOptimizer optimizer = getMultivariateOptimizer();
        OneDimLine line=new OneDimLine();
        PointValuePair optimum =
                optimizer.optimize(new MaxEval(NOF_EVAL_MAX),
                        line.getObjectiveFunction(),
                        line.getGradient(),
                        GoalType.MINIMIZE,
                        new InitialGuess(initialGuess));
        printAndAssert(optimum);
    }

    @NotNull
    private static MultivariateOptimizer getMultivariateOptimizer() {
        return new NonLinearConjugateGradientOptimizer(
                NonLinearConjugateGradientOptimizer.Formula.POLAK_RIBIERE,
                new SimpleValueChecker(RELATIVE_THRESHOLD, ABSOLUTE_THRESHOLD));
    }

    private static void printAndAssert(PointValuePair optimum) {
        System.out.println("optimum.getPointRef() = " + Arrays.toString(optimum.getPointRef()));
        double x=optimum.getPointRef()[0];
        System.out.println("x = " + x);
    }
}
