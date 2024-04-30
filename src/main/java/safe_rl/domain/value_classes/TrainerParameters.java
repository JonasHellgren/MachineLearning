package safe_rl.domain.value_classes;

import lombok.Builder;
import lombok.With;

import static common.other.MyFunctions.defaultIfNullDouble;
import static common.other.MyFunctions.defaultIfNullInteger;

public record TrainerParameters(
        @With Integer nofEpisodes,
        Integer nofStepsMax,
        @With Double gamma,
        @With Double learningRateReplayBufferCritic,
        @With Integer replayBufferSize,
        @With Integer miniBatchSize,
        @With Integer nReplayBufferFitsPerEpisode,
        @With  Double ratioPenCorrectedAction,
        @With Integer stepHorizon) {

    static final int NOF_EPISODES = 2000;
    static final int NOF_STEPS = 100;
    static final double DEF_GAMMA = 0.99;
    static final double LEARNING_RATE = 0.01;
    public static final int STEP_HORIZON = 10;
    public static final double RATIO_PEN_ACTION = 1d;
    public static final int BATCH_SIZE = 10;
    public static final int REPLAY_BUFFER_SIZE = 1000;
    public static final int N_RP_FITS = 1;


    public static TrainerParameters newDefault() {
        return TrainerParameters.builder().build();
    }

    @Builder
    public TrainerParameters(Integer nofEpisodes,
                             Integer nofStepsMax,
                             Double gamma,
                             Double learningRateReplayBufferCritic,
                             Integer replayBufferSize,
                             Integer miniBatchSize,
                             Integer nReplayBufferFitsPerEpisode,
                             Double ratioPenCorrectedAction,
                             Integer stepHorizon) {
        this.nofEpisodes = defaultIfNullInteger.apply(nofEpisodes, NOF_EPISODES);
        this.nofStepsMax = defaultIfNullInteger.apply(nofStepsMax, NOF_STEPS);
        this.gamma = defaultIfNullDouble.apply(gamma, DEF_GAMMA);
        this.replayBufferSize = defaultIfNullInteger.apply(replayBufferSize, REPLAY_BUFFER_SIZE);
        this.miniBatchSize = defaultIfNullInteger.apply(miniBatchSize, BATCH_SIZE);
        this.nReplayBufferFitsPerEpisode = defaultIfNullInteger.apply(nReplayBufferFitsPerEpisode, N_RP_FITS);
        this.ratioPenCorrectedAction = defaultIfNullDouble.apply(ratioPenCorrectedAction, RATIO_PEN_ACTION);
        this.learningRateReplayBufferCritic = defaultIfNullDouble.apply(learningRateReplayBufferCritic, LEARNING_RATE);
        this.stepHorizon = defaultIfNullInteger.apply(stepHorizon, STEP_HORIZON);
    }

    public double gammaPowN() {
        return Math.pow(gamma(), stepHorizon);
    }


}
