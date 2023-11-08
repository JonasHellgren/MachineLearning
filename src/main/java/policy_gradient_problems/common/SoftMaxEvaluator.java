package policy_gradient_problems.common;

import java.util.List;
import java.util.stream.IntStream;

public class SoftMaxEvaluator {

    public static   double[] getProbabilities(List<Double> thetaList ) {
        int nofActions=thetaList.size();
        double sumExpPhi = IntStream.range(0, nofActions).mapToDouble(a -> Math.exp(thetaList.get(a))).sum();
        return IntStream.range(0, nofActions).mapToDouble(a -> Math.exp(thetaList.get(a)) / sumExpPhi).toArray();
    }
}
