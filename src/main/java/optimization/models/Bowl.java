package optimization.models;

import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunctionGradient;

public class Bowl {

    public ObjectiveFunction getObjectiveFunction() {
        return new ObjectiveFunction(point -> {
            double a = point[0];
            double b = point[1];
            return a * a + b * b;
        });
    }

    public ObjectiveFunctionGradient getGradient() {
        return new ObjectiveFunctionGradient(point -> {
            double a = point[0];
            double b = point[1];
            return new double[]{  2 * a ,  2 * b  }; // Gradient: [2a, 2b]
        });
    }

    /**
     * https://en.wikipedia.org/wiki/Finite_difference
     */
    public ObjectiveFunctionGradient getFiniteDiffGradient(double eps) {
        return new ObjectiveFunctionGradient(point -> {
            double[] gradient = new double[point.length];
            double[] forwardPerturbedPoints = new double[point.length];
            double[] backwardPerturbedPoints = new double[point.length];
            ObjectiveFunction function=getObjectiveFunction();
            System.arraycopy(point, 0, forwardPerturbedPoints, 0, point.length);
            System.arraycopy(point, 0, backwardPerturbedPoints, 0, point.length);

            for (int i = 0; i < point.length; i++) {
                backwardPerturbedPoints[i] -= eps;
                forwardPerturbedPoints[i] += eps;
                double fBackward = function.getObjectiveFunction().value(backwardPerturbedPoints);
                double fCenter = function.getObjectiveFunction().value(point);
                double fForward = function.getObjectiveFunction().value(forwardPerturbedPoints);
                gradient[i] = 0.5*(fForward - fCenter) / eps+0.5*(fCenter-fBackward) / eps;
            }

            return gradient;
        });
    }

}
