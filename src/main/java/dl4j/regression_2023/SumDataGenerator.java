package dl4j.regression_2023;

import common.ListUtils;
import lombok.Builder;
import lombok.NonNull;
import org.apache.commons.math3.util.Pair;

import java.util.ArrayList;
import java.util.List;

import static common.RandUtils.getRandomDouble;

@Builder
public class SumDataGenerator {

    @NonNull Double  minValue,maxValue;
    @NonNull Integer nSamplesPerEpoch;

    public   Pair<List<List<Double>> , List<Double>> getTrainingData() {

        List<List<Double>> in=new ArrayList<>();
        List<Double> out=new ArrayList<>();
        for (int i = 0; i< nSamplesPerEpoch; i++) {
            List<Double> inputs = List.of(getRandomDouble(minValue, maxValue), getRandomDouble(minValue, maxValue));
            in.add(inputs);
            out.add(ListUtils.sumList(inputs));
        }
        return Pair.create(in,out);
    }
}
