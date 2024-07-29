package safe_rl.domain.agent.value_objects;

import lombok.Builder;
import safe_rl.domain.trainer.value_objects.TrainerParameters;
import static common.other.MyFunctions.defaultIfNullDouble;

/***
 *  Dummy class, just replaces value with default if field is null
 */

@Builder
public record TrainerParametersInterpreter(
        Double learningRateActorMean,
        Double learningRateActorStd,
        Double learningRateCritic,
        Double targetMean,
        Double absActionNominal,
        Double targetLogStd,
        Double targetCritic,
        Double gradMaxActor,
        Double gradMaxCritic
) {

    public static final double LEARNING_RATE = 1e-2;
    public static final double STD_MIN = 0.01;
    public static final double SMALLEST_DENOM = 1e-5;
    public static final double MAX_GRAD_ELEMENT = 1;
    public static final double SOC_MIN = 0d;
    public static final double TAR_MEAN = 1d;
    public static final double TAR_LOG_STD = 5d;
    public static final double TAR_CRITIC = 0d;
    public static final double STD_TAR = 0d;
    public static final double GRADIENT_MAX = 10d;
    public static final double ABS_TAR_MEAN = 1d;

    public static TrainerParametersInterpreter ofTrainerParams(TrainerParameters p) {
        return  TrainerParametersInterpreter.builder()
                .learningRateActorMean(defaultIfNullDouble.apply(p.learningRateReplayBufferActor(), LEARNING_RATE))
                .learningRateActorStd(defaultIfNullDouble.apply(p.learningRateReplayBufferActorStd(), LEARNING_RATE))
                .learningRateCritic(defaultIfNullDouble.apply(p.learningRateReplayBufferCritic(), LEARNING_RATE))
                .targetMean(defaultIfNullDouble.apply(p.targetMean(), TAR_MEAN))
                .absActionNominal(defaultIfNullDouble.apply(p.absActionNominal(), ABS_TAR_MEAN))
                .targetLogStd(defaultIfNullDouble.apply(p.targetLogStd(), TAR_LOG_STD))
                .targetCritic(defaultIfNullDouble.apply(p.targetCritic(), TAR_CRITIC))
                .gradMaxActor(defaultIfNullDouble.apply(p.gradActorMax(), GRADIENT_MAX))
                .gradMaxCritic(defaultIfNullDouble.apply(p.gradCriticMax(), GRADIENT_MAX))
                .build();
    }

}
