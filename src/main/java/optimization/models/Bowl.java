package optimization.models;

import optimization.helpers.FiniteDiffGradientFactory;
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

    public ObjectiveFunctionGradient getFiniteDiffGradient(double eps) {
        FiniteDiffGradientFactory finiteDiffGradient = new FiniteDiffGradientFactory(getObjectiveFunction(), eps);
        return finiteDiffGradient.getFiniteDiffGradient();
    }

}
