package optimization.models;

import common.ArrayUtil;
import common.ListUtils;
import optimization.helpers.BoundConstraints;
import optimization.helpers.FiniteDiffGradientFactory;
import optimization.helpers.BarrierFunctions;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunctionGradient;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
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
    FiniteDiffGradientFactory finiteDiffGradient;

    public SumOfThree(double penCoeff, double eps) {
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
        return A0 * vars.xList[0] + A1 * vars.xList[1] + A2 * vars.xList[2];
    }

    /**
     * the constraint is violated (i.e., ci(vars) > 0)
     */

    private double getPenalty(Variables vars) {
        int nofVars = vars.xList.length;
        double[] lowerBoundsConstrValues = BoundConstraints.getLowerBoundConstraintValues(vars.xList, LB);
        double[] upperBoundsConstrValues = BoundConstraints.getUpperBoundConstraintValues(vars.xList, UB);
        List<Double> penalties = new ArrayList<>();
        for (int i = 0; i < nofVars; i++) {
            penalties.add(barrier.process(lowerBoundsConstrValues[i]));
            penalties.add(barrier.process(upperBoundsConstrValues[i]));
        }
        penalties.add(barrier.process(getSumOfVarsConstraintValue(vars)));
        return ListUtils.sumList(penalties);
    }

    private static double getSumOfVarsConstraintValue(Variables vars) {
        double sum = ArrayUtil.sum(vars.xList);
        return sum - SUM_MAX;
    }

    public ObjectiveFunctionGradient getWrongGradient() {
        return new ObjectiveFunctionGradient(point -> new double[]{A0, A1, A2});
    }

    public ObjectiveFunctionGradient getFiniteDiffGradient() {
        return finiteDiffGradient.getFiniteDiffGradient();
    }

}
