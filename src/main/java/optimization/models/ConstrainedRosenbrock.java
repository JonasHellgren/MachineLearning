package optimization.models;

import common.ListUtils;
import optimization.helpers.BarrierFunctions;
import optimization.helpers.FiniteDiffGradientFactory;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunctionGradient;

import java.util.ArrayList;
import java.util.List;

/***
 * https://en.wikipedia.org/wiki/Test_functions_for_optimization
 */

public class ConstrainedRosenbrock {

    public static final double LB = -1.5, UB = 1.5;
    private static final double[] LB_ARR = {LB, LB};
    private static final double[] UB_ARR = {UB, UB};

    static class Variables {
        public double[] xList;
    }

    BarrierFunctions barrier;

    public ConstrainedRosenbrock(double penCoeff) {
        this.barrier = new BarrierFunctions(penCoeff, "quad");
    }

    public ObjectiveFunction getObjectiveFunction() {
        return new ObjectiveFunction(point -> {
            Variables var = new Variables();
            var.xList = point;
            return getObjective(var) + getPenalty(var);
        });
    }

    private static double getObjective(Variables vars) {
        double x = vars.xList[0], y = vars.xList[1];
        return Math.pow(1 - x, 2) + 100 * Math.pow(y - Math.pow(x, 2), 2);
    }

    /**
     * the constraint is violated (i.e., ci(vars) > 0)
     */

    private double getPenalty(Variables vars) {
        int nofVars = vars.xList.length;
        double[] lowerBoundsConstrValues = getLowerBoundConstraintValues(vars, nofVars);
        double[] upperBoundsConstrValues = getUpperBoundConstraintValues(vars, nofVars);
        List<Double> penalties = new ArrayList<>();
        for (int i = 0; i < nofVars; i++) {
            penalties.add(barrier.process(lowerBoundsConstrValues[i]));
            penalties.add(barrier.process(upperBoundsConstrValues[i]));
        }
        penalties.add(barrier.process(getSumOfVarsSquaredConstraintValue(vars)));
        return ListUtils.sumList(penalties);
    }

    private static double[] getLowerBoundConstraintValues(Variables vars, int nofVars) {
        double[] lowerBoundsConstrValues = new double[nofVars];
        for (int i = 0; i < nofVars; i++) {
            lowerBoundsConstrValues[i] = LB_ARR[i] - vars.xList[i];
        }
        return lowerBoundsConstrValues;
    }

    private static double[] getUpperBoundConstraintValues(Variables vars, int nofVars) {
        double[] lowerBoundsConstrValues = new double[nofVars];
        for (int i = 0; i < nofVars; i++) {
            lowerBoundsConstrValues[i] = vars.xList[i] - UB_ARR[i];
        }
        return lowerBoundsConstrValues;
    }

    private static double getSumOfVarsSquaredConstraintValue(Variables vars) {
        double x = vars.xList[0], y = vars.xList[1];
        double sumSquares = Math.pow(x, 2) + Math.pow(y, 2);
        return sumSquares - 2;
    }

    public ObjectiveFunctionGradient getFiniteDiffGradient(double eps) {
        FiniteDiffGradientFactory finiteDiffGradient = new FiniteDiffGradientFactory(getObjectiveFunction(), eps);
        return finiteDiffGradient.getFiniteDiffGradient();
    }

}
