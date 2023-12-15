package policy_gradient_problems.common_value_classes;

import common.MyFunctions;
import lombok.Builder;

import static common.MyFunctions.defaultIfNullDouble;
import static common.MyFunctions.defaultIfNullInteger;

public record TrainerParameters(
        Integer nofEpisodes,
        Integer nofStepsMax,
        Double gamma,
        Double learningRateCritic,
        Double learningRateActor,
        Integer stepHorizon,
        Integer nofFitsPerEpoch) {

    static final int NOF_EPISODES = 2000;
    static final int NOF_STEPS = 100;
    static final double GAMMA = 0.99;
    static final double LEARNING_RATE = 0.01;
    public static final int STEP_HORIZON = 10;
    public static final int NOF_FITS_PER_EPOCH = 10;

    public static TrainerParameters newDefault() {
        return TrainerParameters.builder().build();
    }

    @Builder
    public TrainerParameters(Integer nofEpisodes,
                             Integer nofStepsMax,
                             Double gamma,
                             Double learningRateCritic,
                             Double learningRateActor,
                             Integer stepHorizon,
                             Integer nofFitsPerEpoch) {
        this.nofEpisodes = defaultIfNullInteger.apply(nofEpisodes, NOF_EPISODES);
        this.nofStepsMax = defaultIfNullInteger.apply(nofStepsMax, NOF_STEPS);
        this.gamma = defaultIfNullDouble.apply(gamma, GAMMA);
        this.learningRateCritic = defaultIfNullDouble.apply(learningRateCritic, LEARNING_RATE);
        this.learningRateActor = defaultIfNullDouble.apply(learningRateActor, LEARNING_RATE);
        this.stepHorizon = defaultIfNullInteger.apply(stepHorizon, STEP_HORIZON);
        this.nofFitsPerEpoch = defaultIfNullInteger.apply(nofFitsPerEpoch, NOF_FITS_PER_EPOCH);
    }
}
