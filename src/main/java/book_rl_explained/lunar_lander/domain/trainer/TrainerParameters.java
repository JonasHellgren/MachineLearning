package book_rl_explained.lunar_lander.domain.trainer;

import lombok.With;

@With
public record TrainerParameters(
        int nofStepsMax,
        int nEpisodes,
        double gamma,
        double tdMax
) {

    public static TrainerParameters defaultParams() {
        return new TrainerParameters(1000,1000,0.99,10d);
    }

}
