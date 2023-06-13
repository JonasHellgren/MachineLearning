package optimization.models;

import common.ListUtils;
import optimization.helpers.BarrierFunctions;
import optimization.helpers.BoundConstraints;
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
    FiniteDiffGradientFactory finiteDiffGradient;

    public ConstrainedRosenbrock(double penCoeff, double eps) {
        this.barrier = new BarrierFunctions(penCoeff, "quad");
        this.finiteDiffGradient = new FiniteDiffGradientFactory(getObjectiveFunction(), eps);
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
        double[] lowerBoundsConstrValues = BoundConstraints.getLowerBoundConstraintValues(vars.xList, LB_ARR);
        double[] upperBoundsConstrValues = BoundConstraints.getUpperBoundConstraintValues(vars.xList, UB_ARR);
        List<Double> penalties = new ArrayList<>();
        for (int i = 0; i < nofVars; i++) {
            penalties.add(barrier.process(lowerBoundsConstrValues[i]));
            penalties.add(barrier.process(upperBoundsConstrValues[i]));
        }
        penalties.add(barrier.process(getSumOfVarsSquaredConstraintValue(vars)));
        return ListUtils.sumList(penalties);
    }



    private static double getSumOfVarsSquaredConstraintValue(Variables vars) {
        double x = vars.xList[0], y = vars.xList[1];
        double sumSquares = Math.pow(x, 2) + Math.pow(y, 2);
        return sumSquares - 2;
    }

    public ObjectiveFunctionGradient getFiniteDiffGradient() {
        return finiteDiffGradient.getFiniteDiffGradient();
    }

}
