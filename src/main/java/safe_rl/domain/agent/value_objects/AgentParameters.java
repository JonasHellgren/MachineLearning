package safe_rl.domain.agent.value_objects;

import lombok.Builder;
import lombok.NonNull;
import lombok.With;

@Builder
@NonNull
@With
public record AgentParameters (
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
    
    public static AgentParameters newDefault() {
        return AgentParameters.builder()
                .learningRateActorMean(LEARNING_RATE)
                .learningRateActorStd(LEARNING_RATE)
                .learningRateCritic(LEARNING_RATE)
                .targetMean(TAR_MEAN)
                .absActionNominal(ABS_TAR_MEAN)
                .targetLogStd(TAR_LOG_STD)
                .targetCritic(TAR_CRITIC)
                .gradMaxActor(GRADIENT_MAX)
                .gradMaxCritic(GRADIENT_MAX)
                .build();
        
    }
    
}
