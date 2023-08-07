package multi_step_temp_diff.domain.normalizer;

import common.MathUtils;
import lombok.ToString;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

/***
 * https://www.codecademy.com/article/normalization
 */

@ToString
public class NormalizerMeanStd implements NormalizerInterface {

    double mean, std;

    public NormalizerMeanStd(List<Double> valueList) {

        if (valueList.isEmpty() || valueList.size()==1) {
            throw new IllegalArgumentException("Empty or single element value list");
        }

        DoubleSummaryStatistics sumStats=valueList.stream().mapToDouble(v -> v).summaryStatistics();
        mean=sumStats.getAverage();
        List<Double> sumSqr=valueList.stream().map(v -> Math.pow(v-mean,2)).toList();
        System.out.println("sumSqr = " + sumSqr);
        std=Math.sqrt(sumSqr.stream().mapToDouble(Double::doubleValue).sum()/(valueList.size()-1));

        if (MathUtils.isZero(std)) {
            throw new IllegalArgumentException("Value list gives zero standard deviation");

        }

    }

    @Override
    public double normalize(double out) {
        return (out-mean)/std;
    }

    @Override
    public double normalizeInverted(double in) {
        return in*std+mean;
    }
}
