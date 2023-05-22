package optimization.models;

import common.ArrayUtil;
import common.ListUtils;
import optimization.helpers.FiniteDiffGradientFactory;
import optimization.helpers.BarrierFunctions;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunctionGradient;

import java.util.ArrayList;
import java.util.List;

/**
 * sum=x0*A0+x1*A1+x2*A2
 * wrt
 * LB<xi<UB
 * x0+x1+x2<sumMax
 * <p>
 * The consequence is that the most effecting variable is as large as possible, the rest are zero
 * Hence opt point is {0, 0, 1.0};
 */

public class SumOfThree {
    public static final double A0 = -0, A1 = -1, A2 = -2;
    public static final double LB = 0, UB = 1;
    public static final double SUM_MAX = 1;

    static class Variables {
        public double[] xList;
    }

    BarrierFunctions barrier;

    public SumOfThree(double penCoeff) {
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
        return A0 * vars.xList[0] + A1 * vars.xList[1] + A2 * vars.xList[2];
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
        penalties.add(barrier.process(getSumOfVarsConstraintValue(vars)));
        return ListUtils.sumList(penalties);
    }

    private static double[] getLowerBoundConstraintValues(Variables vars, int nofVars) {
        double[] lowerBoundsConstrValues = new double[nofVars];
        for (int i = 0; i < nofVars; i++) {
            lowerBoundsConstrValues[i] = LB - vars.xList[i];
        }
        return lowerBoundsConstrValues;
    }

    private static double[] getUpperBoundConstraintValues(Variables vars, int nofVars) {
        double[] lowerBoundsConstrValues = new double[nofVars];
        for (int i = 0; i < nofVars; i++) {
            lowerBoundsConstrValues[i] = vars.xList[i] - UB;
        }
        return lowerBoundsConstrValues;
    }

    private static double getSumOfVarsConstraintValue(Variables vars) {
        double sum = ArrayUtil.sum(vars.xList);
        return sum - SUM_MAX;
    }

    public ObjectiveFunctionGradient getWrongGradient() {
        return new ObjectiveFunctionGradient(point -> new double[]{A0, A1, A2});
    }

    public ObjectiveFunctionGradient getFiniteDiffGradient(double eps) {
        FiniteDiffGradientFactory finiteDiffGradient = new FiniteDiffGradientFactory(getObjectiveFunction(), eps);
        return finiteDiffGradient.getFiniteDiffGradient();
    }

}
