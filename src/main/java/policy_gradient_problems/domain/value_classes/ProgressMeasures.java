package policy_gradient_problems.domain.value_classes;

import lombok.Builder;
import lombok.With;

import static common.other.MyFunctions.defaultIfNullDouble;
import static common.other.MyFunctions.defaultIfNullInteger;

@Builder
public record ProgressMeasures(
        @With Double sumRewards,
        @With Integer nSteps,
//        @With Double powerCap,
        @With Double actionChange,
        @With Double eval,
        @With Double criticLoss,
        @With Double actorLoss,
        @With Double entropy)
{
    public static final int N_STEPS = 0;
    public static final double DEFAULT_VALUE = 0d;

    public static ProgressMeasures ofAllZero() {
        return ProgressMeasures.builder()
                .sumRewards(0d)
                .nSteps(0)
                .actionChange(0d)
                .eval(0d)
                .criticLoss(0d)
                .actorLoss(0d)
                .entropy(0d)
                .build();
    }


}
