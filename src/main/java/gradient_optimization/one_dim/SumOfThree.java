package gradient_optimization.one_dim;

import common.ArrayUtil;
import common.ListUtils;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunctionGradient;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

public class SumOfThree {
    public static final double A0 = -0, A1=-1, A2=-2;
    public static final double LB = 0, UB = 1;
    public static final double SUM_MAX = 1;

        static class Variables {
        public double[] xList;
    }

    public  double penCoeff = 1.0e1;

    public SumOfThree(double penCoeff) {
        this.penCoeff = penCoeff;
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

    private  double getPenalty(Variables vars) {
        int nofVars=vars.xList.length;

        double[] lowerBoundsConstrValues = getLowerBoundConstraintValues(vars, nofVars);
        double[] upperBoundsConstrValues = getUpperBoundConstraintValues(vars, nofVars);
        List<Double> penalties=new ArrayList<>();
        for (int i = 0; i < nofVars ; i++) {
            penalties.add(getPenalty(lowerBoundsConstrValues[i]));
            penalties.add(getPenalty(upperBoundsConstrValues[i]));
        }

        penalties.add(getPenalty(getSumOfVarsConstraintValue(vars, nofVars)));
      //  System.out.println("penalties = " + penalties);


        return ListUtils.sumList(penalties);
    }

    private  double getPenalty(double constraintValue) {
        return (constraintValue >0)
                ? penCoeff *Math.pow(constraintValue,2)
                : 0d;
    }

    @NotNull
    private static double[] getLowerBoundConstraintValues(Variables vars, int nofVars) {
        double[] lowerBoundsConstrValues=new double[nofVars];
        for (int i = 0; i < nofVars; i++) {
            lowerBoundsConstrValues[i]= LB - vars.xList[i];
        }
        return lowerBoundsConstrValues;
    }

    @NotNull
    private static double[] getUpperBoundConstraintValues(Variables vars, int nofVars) {
        double[] lowerBoundsConstrValues = new double[nofVars];
        for (int i = 0; i < nofVars; i++) {
            lowerBoundsConstrValues[i] = vars.xList[i] - UB;
        }
        return lowerBoundsConstrValues;
    }

    @NotNull
    private static double getSumOfVarsConstraintValue(Variables vars, int nofVars) {
        double sum= ArrayUtil.sum(vars.xList);
      //  List<Double> list = DoubleStream.of(vars.xList).boxed().collect(Collectors.toList());
       // double sum=ListUtils.sumList(list);
        return sum-SUM_MAX;
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
