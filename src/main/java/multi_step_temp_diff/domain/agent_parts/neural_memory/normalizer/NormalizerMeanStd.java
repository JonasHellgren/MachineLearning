package multi_step_temp_diff.domain.agent_parts.neural_memory.normalizer;

import common.MathUtils;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.java.Log;

import java.util.DoubleSummaryStatistics;
import java.util.List;

/***
 * https://www.codecademy.com/article/normalization
 */

@ToString
@Log
@Getter
public class NormalizerMeanStd implements NormalizerInterface {

    double mean, std;
    List<Double> valueList;

    public NormalizerMeanStd(List<Double> valueList) {

        this.valueList=valueList;

        if (valueList.isEmpty() || valueList.size()==1) {
            throw new IllegalArgumentException("Empty or single element value list");
        }

        DoubleSummaryStatistics sumStats=valueList.stream().mapToDouble(v -> v).summaryStatistics();
        mean=sumStats.getAverage();
        List<Double> sumSqr=valueList.stream().map(v -> Math.pow(v-mean,2)).toList();
        std=Math.sqrt(sumSqr.stream().mapToDouble(Double::doubleValue).sum()/(valueList.size()-1));

        if (MathUtils.isZero(std)) {
            throw new IllegalArgumentException("Value list gives zero standard deviation");

        }

        log.fine("Normalizer with mean = "+mean + " and std = "+ std);

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
