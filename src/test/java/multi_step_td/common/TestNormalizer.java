package multi_step_td.common;

import multi_step_temp_diff.domain.agent_abstract.normalizer.NormalizeMinMax;
import multi_step_temp_diff.domain.agent_abstract.normalizer.NormalizerInterface;
import multi_step_temp_diff.domain.agent_abstract.normalizer.NormalizerMeanStd;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestNormalizer {


    @ParameterizedTest
    @CsvSource({"1, 0.01","11,0.11","77,0.77"})
    void whenMinMax_thenCorrect(ArgumentsAccessor arguments) {
        NormalizerInterface normalizer=new NormalizeMinMax(0,100);
        double value=arguments.getDouble(0);
        double valueNormalizedExpected=arguments.getDouble(1);
        assertEquals(valueNormalizedExpected,normalizer.normalize(value));
    }

    /**
     * To perform z-normalization (also known as standardization), you need to calculate the z-score for each value.
     * The z-score measures how many standard deviations a data point is away from the mean.
     * The formula to calculate the z-score is:
     * z = (x - mean) / standard deviation
     * Where:
     * x = the data point
     * mean = the mean of the data set
     * standard deviation = the standard deviation of the data set
     * Since we don't have the complete data set, we cannot calculate the exact mean and standard deviation.
     * However, assuming you have the entire data set, let's say the mean (μ) is 0.6 and the standard deviation (σ)
     * is 0.2, then you can calculate the z-scores for each value:
     * z(0.3) = (0.3 - 0.6) / 0.2582 = -1.1619
     * z(0.5) = (0.5 - 0.6) / 0.2582 = -0.3873
     * z(0.7) = (0.7 - 0.6) / 0.2582 = 0.3873
     * z(0.9) = (0.9 - 0.6) / 0.2582 = 1.1619
     */

    @ParameterizedTest
    @CsvSource({"0.3, -1.1619","0.5,-0.3873","0.7,0.3873","0.9,1.1619"})
    void whenMeanStd_thenCorrect(ArgumentsAccessor arguments) {
        NormalizerInterface normalizer=new NormalizerMeanStd(List.of(0.3,0.5,0.7,0.9));

        System.out.println("valueNormalizer = " + normalizer);

        double value=arguments.getDouble(0);
        double valueNormalizedExpected=arguments.getDouble(1);
        assertEquals(valueNormalizedExpected,normalizer.normalize(value),0.01);
    }


}
