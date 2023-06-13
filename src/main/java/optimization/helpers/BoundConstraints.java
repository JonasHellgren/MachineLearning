package optimization.helpers;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 *   x>=LB =>  cv=LB-x;  violation <=> cv>0
 *   x<=UB =>  cv=x-UB;  violation <=> cv>0
 *
 */


public class BoundConstraints {

    public static double[] getLowerBoundConstraintValues(double[] xList, double[] lowerBounds) {
        int nofVars = getNofVars(xList);
        double[] constraintValues = new double[nofVars];
        for (int i = 0; i < nofVars; i++) {
            constraintValues[i] = lowerBounds[i] - xList[i];
        }
        return constraintValues;
    }

    public static double[] getUpperBoundConstraintValues(double[] xList, double[] upperBounds) {
        int nofVars = getNofVars(xList);
        double[] constraintValues = new double[nofVars];
        for (int i = 0; i < nofVars; i++) {
            constraintValues[i] = xList[i] - upperBounds[i];
        }
        return constraintValues;
    }

    public static double[] getLowerBoundConstraintValues(double[] xList, double value) {
        return getLowerBoundConstraintValues(xList,getDoubles(getNofVars(xList), value));
    }

    public static double[] getUpperBoundConstraintValues(double[] xList, double value) {
        return getUpperBoundConstraintValues(xList,getDoubles(getNofVars(xList), value));
    }

    private static double[] getDoubles(int nofVars, double value) {
        double[] lowerBounds=new double[nofVars];
        Arrays.fill(lowerBounds, value);
        return lowerBounds;
    }


    public static int getNofVars(double[] xList) {
        return xList.length;
    }

}
