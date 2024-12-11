package book_rl_explained.lunar_lander.domain.agent;

import org.hellgren.utilities.math.MyMathUtils;

/**
 * Represents a gradient of the mean and standard deviation of a normal distribution.
 */

public record GradientMeanStd(MeanStd meanStd) {

    public static GradientMeanStd of(double gradMean, double gradLogStd) {
        return new GradientMeanStd(new MeanStd(gradMean, gradLogStd));
    }

    /**
     * Clips the gradient mean and standard deviation to the given maximum values.
     *
     * @param meanMax the maximum value for the gradient mean
     * @param stdMax the maximum value for the gradient standard deviation
     * @return a new GradientMeanStd instance with clipped values
     */

    public GradientMeanStd clip(double meanMax, double stdMax) {
        return GradientMeanStd.of(
                MyMathUtils.clip(meanStd.mean(), -meanMax, meanMax),
                MyMathUtils.clip(meanStd.std(), -stdMax, stdMax));
    }

    public double mean() {
        return meanStd.mean();
    }

    public double std() {
        return meanStd.std();
    }
}
