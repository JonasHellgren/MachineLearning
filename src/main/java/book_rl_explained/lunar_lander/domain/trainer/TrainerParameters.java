package book_rl_explained.lunar_lander.domain.trainer;

import lombok.With;

@With
public record TrainerParameters(
        int nofStepsMax,
        int nEpisodes,
        double gamma,
        double tdMax,
        int stepHorizon

) {

    public static TrainerParameters newDefault() {
        return new TrainerParameters(1000,30,0.99,10d,5);
    }

    public double gammaPowN() {
        return Math.pow(gamma, stepHorizon);
    }
}
