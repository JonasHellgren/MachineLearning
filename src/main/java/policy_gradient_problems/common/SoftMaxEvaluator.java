package policy_gradient_problems.common;

import common.ListUtils;

import java.util.List;
import java.util.stream.IntStream;

/***
 * For discrete actions
 * https://en.wikipedia.org/wiki/Softmax_function
 */

public class SoftMaxEvaluator {


    public static double[] getProbabilities(double[] thetaList ) {
        return ListUtils.toArray(getProbabilities(ListUtils.arrayPrimitiveDoublesToList(thetaList)));
    }

        public static List<Double> getProbabilities(List<Double> thetaList ) {
        int nofActions=thetaList.size();
        double sumExpPhi = IntStream.range(0, nofActions).mapToDouble(a -> Math.exp(thetaList.get(a))).sum();
        return ListUtils.arrayPrimitiveDoublesToList(
                IntStream.range(0, nofActions).mapToDouble(a -> Math.exp(thetaList.get(a)) / sumExpPhi).toArray());
    }
}
