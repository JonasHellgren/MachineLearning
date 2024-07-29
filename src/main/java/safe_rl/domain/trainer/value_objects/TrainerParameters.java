package safe_rl.domain.trainer.value_objects;

import lombok.Builder;
import lombok.NonNull;
import lombok.With;

import static common.other.MyFunctions.*;

@Builder
@With
@NonNull
public record TrainerParameters(
        Integer nofEpisodes,
        Integer nofStepsMax,
        Integer stepHorizon,
        Double gamma,
        Double learningRateReplayBufferCritic,
        Double learningRateReplayBufferActor,
        Double learningRateReplayBufferActorStd,
        Integer replayBufferSize,
        Integer miniBatchSize,
        Integer nReplayBufferFitsPerEpisode,
        Boolean isRemoveOldest,
        Double ratioPenCorrectedAction) {

    static final int NOF_EPISODES = 2000;
    static final int NOF_STEPS = 100;
    static final double DEF_GAMMA = 0.99;
    static final double LEARNING_RATE = 0.01;
    static final double LEARNING_RATE_SMALL = 1e-4;
    public static final int STEP_HORIZON = 10;
    public static final double RATIO_PEN_ACTION = 0.1d;
    public static final int BATCH_SIZE = 10;
    public static final int REPLAY_BUFFER_SIZE = 1000;
    public static final int N_RP_FITS = 3;
    public static final double GRAD_MAX = 1e-3;
    public static final boolean IS_REMOVE_OLDEST = true;
    public static final double TAR_MEAN = 1d;
    public static final double TAR_LOG = Math.log(1);
    public static final double TAR_CRIT = 0d;
    public static final double ABS_NOM = 1d;

    public static TrainerParameters newDefault() {
        return TrainerParameters.builder()
                .nofEpisodes(NOF_EPISODES)
                .nofStepsMax(NOF_STEPS)
                .stepHorizon(STEP_HORIZON)
                .gamma(DEF_GAMMA)
                .learningRateReplayBufferCritic(LEARNING_RATE)
                .learningRateReplayBufferActor(LEARNING_RATE_SMALL)
                .learningRateReplayBufferActorStd(LEARNING_RATE_SMALL)
                .replayBufferSize(REPLAY_BUFFER_SIZE)
                .miniBatchSize(BATCH_SIZE)
                .nReplayBufferFitsPerEpisode(N_RP_FITS)
                .isRemoveOldest(IS_REMOVE_OLDEST)
                .ratioPenCorrectedAction(RATIO_PEN_ACTION)
                .build();
    }

    public double gammaPowN() {
        return Math.pow(gamma, stepHorizon);
    }


}
