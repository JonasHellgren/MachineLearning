package policy_gradient_problems.domain.value_classes;

import lombok.Builder;
import lombok.With;

import static common.other.MyFunctions.defaultIfNullDouble;
import static common.other.MyFunctions.defaultIfNullInteger;

public record TrainerParameters(
        Integer nofEpisodes,
        Integer nofStepsMax,
        @With Double gamma,
        Double learningRateNonNeuralActor,
        @With Integer stepHorizon) {

    static final int NOF_EPISODES = 2000;
    static final int NOF_STEPS = 100;
    static final double DEF_GAMMA = 0.99;
    static final double LEARNING_RATE = 0.01;
    public static final int STEP_HORIZON = 10;


    public static TrainerParameters newDefault() {
        return TrainerParameters.builder().build();
    }

    @Builder
    public TrainerParameters(Integer nofEpisodes,
                             Integer nofStepsMax,
                             Double gamma,
                             Double learningRateNonNeuralActor,
                             Integer stepHorizon) {
        this.nofEpisodes = defaultIfNullInteger.apply(nofEpisodes, NOF_EPISODES);
        this.nofStepsMax = defaultIfNullInteger.apply(nofStepsMax, NOF_STEPS);
        this.gamma = defaultIfNullDouble.apply(gamma, DEF_GAMMA);
        this.learningRateNonNeuralActor = defaultIfNullDouble.apply(learningRateNonNeuralActor, LEARNING_RATE);
        this.stepHorizon = defaultIfNullInteger.apply(stepHorizon, STEP_HORIZON);
    }


}
