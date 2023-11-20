package policy_gradient_problems.common;

import common.DefaultPredicates;
import lombok.Builder;
import lombok.NonNull;

public record TrainerParameters(
        Integer nofEpisodes,
        Integer nofStepsMax,
        Double gamma,
        Double learningRate) {

    public static final int NOF_EPISODES = 1000;
    public static final int NOF_STEPS = 100;
    public static final double GAMMA = 1;
    public static final double LEARNING_RATE = 0.99;

    @Builder
    public TrainerParameters(Integer nofEpisodes, Integer nofStepsMax, Double gamma, Double learningRate) {
        this.nofEpisodes = DefaultPredicates.defaultIfNullInteger.apply(nofEpisodes, NOF_EPISODES);
        this.nofStepsMax = DefaultPredicates.defaultIfNullInteger.apply(nofStepsMax, NOF_STEPS);
        this.gamma = DefaultPredicates.defaultIfNullDouble.apply(gamma, GAMMA);
        this.learningRate = DefaultPredicates.defaultIfNullDouble.apply(learningRate, LEARNING_RATE);
    }
}
