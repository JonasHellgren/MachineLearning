package gradient_optimization;

import gradient_optimization.one_dim.OneDimLine;
import gradient_optimization.one_dim.SumOfThree;
import org.apache.commons.math3.optim.*;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.MultivariateOptimizer;
import org.apache.commons.math3.optim.nonlinear.scalar.gradient.NonLinearConjugateGradientOptimizer;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.Arrays;

public class TestSumOfThree {

    public static final double DELTA = 1.0e-1;
    public static final int NOF_EVAL_MAX = 10_000;
    public static final double RELATIVE_THRESHOLD = 1e-10;
    public static final double ABSOLUTE_THRESHOLD = 1e-10;
    public static final double EPS = 1e-10;
    double[] initialGuess = {0.5,0.5,0.5};

    @Test
    public void givenPoint000_thenZeroObj() {
        SumOfThree sumOfThree=new SumOfThree();
        System.out.println("value(new double[] {0,0,0}) = "
                + sumOfThree.getObjectiveFunction().getObjectiveFunction().value(new double[]{0, 0, 0}));

        System.out.println("value(new double[] {.45, 0.45, 0}) = "
                + sumOfThree.getObjectiveFunction().getObjectiveFunction().value(new double[]{.45, 0.45, 0}));

        System.out.println("value(new double[] {.77, 0.33, 0}) = "
                + sumOfThree.getObjectiveFunction().getObjectiveFunction().value(new double[]{.77, 0.33, 0}));

    }

    @Test
    public void givenLine_thenZeroIsOptimum() {
        MultivariateOptimizer optimizer = getMultivariateOptimizer();
        SumOfThree sumOfThree=new SumOfThree();

        PointValuePair optimum =
                optimizer.optimize(new MaxEval(NOF_EVAL_MAX),
                        sumOfThree.getObjectiveFunction(),
                        sumOfThree.getGradient(),
                        GoalType.MINIMIZE,
                        new InitialGuess(initialGuess));
        printAndAssert(optimum);
        printOptimizerStats(optimizer);
    }



    @Test
    public void givenLineFiniteDiffGradient_thenZeroIsOptimum() {
        MultivariateOptimizer optimizer = getMultivariateOptimizer();
        SumOfThree sumOfThree=new SumOfThree();
        PointValuePair optimum =
                optimizer.optimize(new MaxEval(NOF_EVAL_MAX),
                        sumOfThree.getObjectiveFunction(),
                        sumOfThree.getFiniteDiffGradient(EPS),
                        GoalType.MINIMIZE,
                        new InitialGuess(initialGuess));
        printAndAssert(optimum);
        printOptimizerStats(optimizer);

    }


    @Test
    public void givenLineFiniteDiffGradientBounds_thenZeroIsOptimum() {
        MultivariateOptimizer optimizer = getMultivariateOptimizer();
        SumOfThree sumOfThree=new SumOfThree();
        double[] lowerBounds = {-1.0, -1.0,-1.0};
        double[] upperBounds = {10.0, 10.0,10.0};
        SimpleBounds bounds = new SimpleBounds(lowerBounds, upperBounds);

        PointValuePair optimum =
                optimizer.optimize(new MaxEval(NOF_EVAL_MAX),
                        sumOfThree.getObjectiveFunction(),
                        sumOfThree.getFiniteDiffGradient(EPS),
                        GoalType.MINIMIZE,
                      //  bounds,  //gives MathUnsupportedOperationException
                        new InitialGuess(initialGuess));
        printAndAssert(optimum);
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

    private static void printAndAssert(PointValuePair optimum) {
        System.out.println("optimum.getPointRef() = " + Arrays.toString(optimum.getPointRef()));
        double x=optimum.getPointRef()[0];
        System.out.println("x = " + x);
    }

}
