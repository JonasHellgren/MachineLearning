package policy_gradient_problems.domain.value_classes;

import lombok.Builder;
import lombok.With;

import static common.other.MyFunctions.defaultIfNullDouble;
import static common.other.MyFunctions.defaultIfNullInteger;

public record ProgressMeasures(
        @With Double sumRewards,
        @With Integer nSteps,
        @With Double eval,
        @With Double criticLoss,
        @With Double actorLoss,
        @With Double entropy)
{
    public static final int N_STEPS = 0;
    public static final double DEFAULT_VALUE = 0d;

    public static ProgressMeasures ofAllZero() {
        return ProgressMeasures.builder().build();
    }

    @Builder
    public ProgressMeasures(Double sumRewards, Integer nSteps, Double eval,
                            Double criticLoss, Double actorLoss,  Double entropy) {
        this.sumRewards = defaultIfNullDouble.apply(sumRewards, DEFAULT_VALUE);
        this.nSteps = defaultIfNullInteger.apply(nSteps, N_STEPS);
        this.eval = defaultIfNullDouble.apply(eval,DEFAULT_VALUE);
        this.criticLoss = defaultIfNullDouble.apply(criticLoss,DEFAULT_VALUE);
        this.actorLoss = defaultIfNullDouble.apply(actorLoss,DEFAULT_VALUE);
        this.entropy = defaultIfNullDouble.apply(entropy,DEFAULT_VALUE);
    }
}
