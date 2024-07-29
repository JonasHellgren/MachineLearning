package safe_rl.domain.trainer.value_objects;

import lombok.Builder;
import lombok.With;

import static common.other.MyFunctions.*;

public record TrainerParameters(
        @With Integer nofEpisodes,
        @With Integer nofStepsMax,
        @With Double gamma,
        @With Double learningRateReplayBufferCritic,
        @With Double learningRateReplayBufferActor,
        @With Double learningRateReplayBufferActorStd,
        @With Double gradActorMax,
        @With Double gradCriticMax,
        @With Double targetMean,
        @With Double absActionNominal,
        @With Double targetLogStd,
        @With Double targetCritic,
        @With Integer replayBufferSize,
        @With Integer miniBatchSize,
        @With Integer nReplayBufferFitsPerEpisode,
        @With Boolean isRemoveOldest,
        @With Double ratioPenCorrectedAction,
        @With Integer stepHorizon) {

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
        return TrainerParameters.builder().build();
    }

    @Builder
    public TrainerParameters(Integer nofEpisodes,
                             Integer nofStepsMax,
                             Double gamma,
                             Double learningRateReplayBufferCritic,
                             Double learningRateReplayBufferActor,
                             Double learningRateReplayBufferActorStd,
                             Double gradActorMax,
                             Double gradCriticMax,
                             Double targetMean,
                             Double absActionNominal,
                             Double targetLogStd,
                             Double targetCritic,
                             Integer replayBufferSize,
                             Integer miniBatchSize,
                             Integer nReplayBufferFitsPerEpisode,
                             Boolean isRemoveOldest,
                             Double ratioPenCorrectedAction,
                             Integer stepHorizon) {
        this.nofEpisodes = defaultIfNullInteger.apply(nofEpisodes, NOF_EPISODES);
        this.nofStepsMax = defaultIfNullInteger.apply(nofStepsMax, NOF_STEPS);
        this.gamma = defaultIfNullDouble.apply(gamma, DEF_GAMMA);
        this.learningRateReplayBufferCritic = defaultIfNullDouble.apply(
                learningRateReplayBufferCritic, LEARNING_RATE);
        this.learningRateReplayBufferActor = defaultIfNullDouble.apply(
                learningRateReplayBufferActor, LEARNING_RATE_SMALL);
        this.learningRateReplayBufferActorStd = defaultIfNullDouble.apply(
                learningRateReplayBufferActorStd, LEARNING_RATE_SMALL);

        this.gradActorMax = defaultIfNullDouble.apply(gradActorMax, GRAD_MAX);
        this.gradCriticMax = defaultIfNullDouble.apply(gradCriticMax, GRAD_MAX);
        this.targetMean = defaultIfNullDouble.apply(targetMean, TAR_MEAN);
        this.absActionNominal = defaultIfNullDouble.apply(absActionNominal, ABS_NOM);
        this.targetLogStd = defaultIfNullDouble.apply(targetLogStd, TAR_LOG);
        this.targetCritic = defaultIfNullDouble.apply(targetCritic, TAR_CRIT);
        this.replayBufferSize = defaultIfNullInteger.apply(replayBufferSize, REPLAY_BUFFER_SIZE);
        this.miniBatchSize = defaultIfNullInteger.apply(miniBatchSize, BATCH_SIZE);
        this.nReplayBufferFitsPerEpisode = defaultIfNullInteger.apply(nReplayBufferFitsPerEpisode, N_RP_FITS);
        this.isRemoveOldest = (Boolean) defaultIfNullObject.apply(isRemoveOldest, IS_REMOVE_OLDEST);
        this.ratioPenCorrectedAction = defaultIfNullDouble.apply(ratioPenCorrectedAction, RATIO_PEN_ACTION);
        this.stepHorizon = defaultIfNullInteger.apply(stepHorizon, STEP_HORIZON);
    }

    public double gammaPowN() {
        return Math.pow(gamma, stepHorizon);
    }


}
