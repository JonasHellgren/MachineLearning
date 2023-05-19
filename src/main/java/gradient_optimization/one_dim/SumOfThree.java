package gradient_optimization.one_dim;

import common.ListUtils;
import common.MathUtils;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunctionGradient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SumOfThree {
    public static final double A0 = 10, A1=10, A2=10;
    public static final double PEN_COEFF = Math.pow(10,5);
    public static final double LB = 0;

    static class Variables {
        public double[] xList;
    }

    public ObjectiveFunction getObjectiveFunction() {
        return new ObjectiveFunction(point -> {
            Variables var= new Variables();
            var.xList= point;
            return getObjective(var)+getPenalty(var);
        });
    }

    private static double getObjective(Variables vars) {
        return A0 * vars.xList[0] + A1*vars.xList[1]+ A2*vars.xList[2];
    }

    /**
     * the constraint is violated (i.e., ci(vars) > 0)
     */

    private static double getPenalty(Variables vars) {
        int nofVars=vars.xList.length;

        double[] lowerBoundsConstrValues=new double[nofVars];
        for (int i = 0; i < nofVars ; i++) {
            lowerBoundsConstrValues[i]= LB -vars.xList[i];
        }

        List<Double> penalties=new ArrayList<>();
        for (int i = 0; i < nofVars ; i++) {
            double constraintValue=lowerBoundsConstrValues[i];
            double penalty=(constraintValue>0)
                    ? PEN_COEFF *Math.pow(constraintValue,2)
                    : 0d;

            penalties.add(penalty);
        }

        return ListUtils.sumList(penalties);
    }

    public ObjectiveFunctionGradient getGradient() {
        return new ObjectiveFunctionGradient(point -> new double[]{A0,A1,A2});
    }

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
