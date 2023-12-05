package policy_gradient_problems.common_value_classes;

import common.MyFunctions;
import lombok.Builder;

public record TrainerParameters(
        Integer nofEpisodes,
        Integer nofStepsMax,
        Double gamma,
        Double beta,
        Double learningRate,
        Integer stepHorizon) {

    static final int NOF_EPISODES = 2000;
    static final int NOF_STEPS = 100;
    static final double GAMMA = 0.99;
    static final double BETA = 0.001;
    static final double LEARNING_RATE = 0.01;
    public static final int STEP_HORIZON = 10;

    public static TrainerParameters newDefault() {
        return TrainerParameters.builder().build();
    }

    @Builder
    public TrainerParameters(Integer nofEpisodes,
                             Integer nofStepsMax,
                             Double gamma,
                             Double beta,
                             Double learningRate,
                             Integer stepHorizon) {
        this.nofEpisodes = MyFunctions.defaultIfNullInteger.apply(nofEpisodes, NOF_EPISODES);
        this.nofStepsMax = MyFunctions.defaultIfNullInteger.apply(nofStepsMax, NOF_STEPS);
        this.gamma = MyFunctions.defaultIfNullDouble.apply(gamma, GAMMA);
        this.beta = MyFunctions.defaultIfNullDouble.apply(beta, BETA);
        this.learningRate = MyFunctions.defaultIfNullDouble.apply(learningRate, LEARNING_RATE);
        this.stepHorizon = MyFunctions.defaultIfNullInteger.apply(stepHorizon, STEP_HORIZON);

    }
}
