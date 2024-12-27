package book_rl_explained.lunar_lander.domain.trainer;

import lombok.With;
import org.hellgren.utilities.math.MyMathUtils;

@With
public record TrainerParameters(
        int nofStepsMax,
        int nEpisodes,
        double gamma,
        int stepHorizon) {

    public static TrainerParameters newDefault() {
        return new TrainerParameters(100,5000,0.99,5);
    }

    public double gammaPowN() {
        return Math.pow(gamma, stepHorizon);
    }

}
