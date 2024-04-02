package policy_gradient_problems.domain.value_classes;

import lombok.Builder;
import static common.MyFunctions.defaultIfNullDouble;
import static common.MyFunctions.defaultIfNullInteger;

public record TrainingMeasures(
        Double sumRewards,
        Integer nSteps,
        Double criticLoss,
        Double actorLoss
)
{

    @Builder
    public TrainingMeasures(Double sumRewards, Integer nSteps, Double criticLoss, Double actorLoss) {
        this.sumRewards = defaultIfNullDouble.apply(sumRewards,0d);
        this.nSteps = defaultIfNullInteger.apply(nSteps,0);
        this.criticLoss = defaultIfNullDouble.apply(criticLoss,0d);
        this.actorLoss = defaultIfNullDouble.apply(actorLoss,0d);
    }
}
