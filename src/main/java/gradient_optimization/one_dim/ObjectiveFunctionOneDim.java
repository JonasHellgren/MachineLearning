package gradient_optimization.one_dim;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunctionGradient;

public class ObjectiveFunctionOneDim  {
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
}