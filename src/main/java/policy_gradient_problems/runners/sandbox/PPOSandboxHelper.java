package policy_gradient_problems.runners.sandbox;

import common.MathUtils;
import lombok.Builder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 *
 * For proximal policy algorithms
 */

@Builder
public class PPOSandboxHelper {

    @Builder.Default
    double  penRatioDeviation=1e-5;

    public static PPOSandboxHelper newDefault() {
        return PPOSandboxHelper.builder().build();
    }

     public double getPPOObjective(double ratio, double advantage, double epsilon) {
        return Math.min(ratio*advantage, MathUtils.clip(ratio,1-epsilon,1+epsilon)*advantage)
                -penRatioDeviation*Math.abs(1-ratio);
    }

    @NotNull
    public static List<Double> divideListElements(List<Double> listA, List<Double> listB) {
        return listB.stream()
                .map(p -> { int i= listB.indexOf(p); return  listB.get(i)/ listA.get(i); })
                .toList();
    }

}
