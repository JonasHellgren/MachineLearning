package book_rl_explained.lunar_lander.domain.agent;

public record MeanAndStd(
        double mean,
        double std
) {

    public static MeanAndStd of(double mean, double std) {
        return new MeanAndStd(mean, std);
    }
}
