package book_rl_explained.lunar_lander.domain.trainer;

import lombok.With;
import org.hellgren.utilities.math.MyMathUtils;

@With
public record TrainerParameters(
        int nofStepsMax,
        int nEpisodes,
        double gamma,
        double tdMax,
        double advMax,
        int stepHorizon

) {

    public static TrainerParameters newDefault() {
        return new TrainerParameters(1000,30,0.99,10d,10d,5);
    }

    public double gammaPowN() {
        return Math.pow(gamma, stepHorizon);
    }

    public double clipAdvantage(double adv0) {
        return MyMathUtils.clip(adv0, -advMax, advMax);
    }

    public double clipTdError(double e0) {
        return MyMathUtils.clip(e0, -tdMax(), tdMax());
    }

}
