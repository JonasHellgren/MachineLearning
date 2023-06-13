package optimization;

import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.SimpleValueChecker;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.MultivariateOptimizer;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunctionGradient;
import org.apache.commons.math3.optim.nonlinear.scalar.gradient.NonLinearConjugateGradientOptimizer;

import java.util.Arrays;

public class TestHelper {

    static void printPointValuePair(PointValuePair optimum) {
        System.out.println("found opt point = " + Arrays.toString(optimum.getPointRef())+", value = "+optimum.getValue());
    }

    static void printOptimizerStats(MultivariateOptimizer optimizer) {
        System.out.println("optimizer.getEvaluations() = " + optimizer.getEvaluations());
        System.out.println("optimizer.getIterations() = " + optimizer.getIterations());
    }

    static MultivariateOptimizer getConjugateGradientOptimizer(double relTresHold, double absTresHold) {
        return new NonLinearConjugateGradientOptimizer(
                NonLinearConjugateGradientOptimizer.Formula.POLAK_RIBIERE,
                new SimpleValueChecker(relTresHold, absTresHold));
    }

    static PointValuePair gradientOptimize(MultivariateOptimizer optimizer,
                                           ObjectiveFunction objFunction,
                                           ObjectiveFunctionGradient gradientFunction,
                                           double[] initialGuess, int nofEvalMax) {
        return optimizer.optimize(new MaxEval(nofEvalMax),
                objFunction, gradientFunction,
                GoalType.MINIMIZE,
                new InitialGuess(initialGuess));
    }

}
