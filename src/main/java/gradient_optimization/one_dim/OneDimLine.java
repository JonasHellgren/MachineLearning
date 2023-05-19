package gradient_optimization.one_dim;
import common.MathUtils;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunctionGradient;

public class OneDimLine  {
    public static final double A = 1;
    public static final double B = 1;
    public static final double PEN_COEFF = Math.pow(10,6);

    static class Variables {
        public double x1;
    }

    public ObjectiveFunction getObjectiveFunction() {
        return new ObjectiveFunction(point -> {
            Variables var= new Variables();
            var.x1=point[0];
            return getObjective(var)+getPenalty(var);
        });
    }

    private static double getObjective(Variables vars) {
        return A * vars.x1 + B;
    }

    /**
     * the constraint is violated (i.e., ci(vars) > 0)
     */

    private static double getPenalty(Variables vars) {
        double constraintValue=0-vars.x1;
        return (MathUtils.isPos(constraintValue))
                ? PEN_COEFF *Math.pow(constraintValue,2)
                : 0;
    }

    public ObjectiveFunctionGradient getGradient() {
        return new ObjectiveFunctionGradient(point -> {
            return new double[]{A}; // Gradient: [A]
        });
    }
}
