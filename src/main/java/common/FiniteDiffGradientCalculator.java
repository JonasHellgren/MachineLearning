package common;

import lombok.Setter;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunctionGradient;

/**
 * https://en.wikipedia.org/wiki/Finite_difference
 */

@Setter
public class FiniteDiffGradientCalculator {

    public static final double ONE_DIV_TWO = 0.5;
    ObjectiveFunction objectiveFunction;
    double eps;

    public FiniteDiffGradientCalculator(ObjectiveFunction objectiveFunction, double eps) {
        this.objectiveFunction = objectiveFunction;
        this.eps = eps;
    }

    public ObjectiveFunctionGradient getFiniteDiffGradient() {
        return new ObjectiveFunctionGradient(point -> {
            double[] gradient = new double[point.length];
            double[] forwardPerturbedPoints = new double[point.length];
            double[] backwardPerturbedPoints = new double[point.length];
            ObjectiveFunction function=objectiveFunction;
            System.arraycopy(point, 0, forwardPerturbedPoints, 0, point.length);
            System.arraycopy(point, 0, backwardPerturbedPoints, 0, point.length);

            for (int i = 0; i < point.length; i++) {
                backwardPerturbedPoints[i] -= eps;
                forwardPerturbedPoints[i] += eps;
                double fBackward = function.getObjectiveFunction().value(backwardPerturbedPoints);
                double fCenter = function.getObjectiveFunction().value(point);
                double fForward = function.getObjectiveFunction().value(forwardPerturbedPoints);
                gradient[i] = ONE_DIV_TWO*(fForward - fCenter) / eps+ ONE_DIV_TWO *(fCenter-fBackward) / eps;
            }

            return gradient;
        });
    }

}
