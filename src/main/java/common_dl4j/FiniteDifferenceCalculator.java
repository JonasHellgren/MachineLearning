package common_dl4j;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.function.Function;

public class FiniteDifferenceCalculator {

    private FiniteDifferenceCalculator() {
    }

    /**
     * Calculates the numerical gradient of a function at a given point using the finite difference method.
     *
     * @param function The function for which to calculate the gradient. It takes an INDArray and returns a double.
     * @param point The point (as an INDArray) at which to calculate the gradient.
     * @param epsilon A small value to use for the finite difference calculation.
     * @return The numerical gradient as an INDArray.
     */
    public static INDArray calculateGradient(Function<INDArray, Double> function, INDArray point, double epsilon) {
        INDArray gradient = Nd4j.zeros(point.length());

        for (int i = 0; i < point.length(); i++) {
            INDArray pointPlusEpsilon = point.dup();
            pointPlusEpsilon.putScalar(i, point.getDouble(i) + epsilon);
            double fPlusEpsilon = function.apply(pointPlusEpsilon);
            INDArray pointMinusEpsilon = point.dup();
            pointMinusEpsilon.putScalar(i, point.getDouble(i) - epsilon);
            double fMinusEpsilon = function.apply(pointMinusEpsilon);
            double derivative = (fPlusEpsilon - fMinusEpsilon) / (2 * epsilon);
            gradient.putScalar(i, derivative);
        }
        return gradient;
    }


}