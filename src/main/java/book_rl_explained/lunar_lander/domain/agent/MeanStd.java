package book_rl_explained.lunar_lander.domain.agent;

/**
 * Represents a mean and standard deviation pair.
 * This record class is used to store  mean and standard deviation values.
 */

public record MeanStd(
        double mean,
        double std
) {

    public static MeanStd of(double mean, double std) {
        return new MeanStd(mean, std);
    }

}
