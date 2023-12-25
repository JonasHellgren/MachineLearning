package dl4j.regression_2023.classes;

import common.ListUtils;
import common_dl4j.Dl4JUtil;
import lombok.Builder;
import lombok.NonNull;
import org.apache.commons.math3.util.Pair;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.cpu.nativecpu.NDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.ArrayList;
import java.util.List;

import static common.RandUtils.getRandomDouble;

@Builder
public class SumDataGenerator {

    public static final int NOF_INPUTS = 2;
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

    public   Pair<INDArray , INDArray> getTrainingDataIndArray() {
        Pair<List<List<Double>> , List<Double>> data0= getTrainingData();
        INDArray in= Dl4JUtil.convertListOfLists(data0.getFirst(), NOF_INPUTS);
        INDArray out= Dl4JUtil.convertList2(data0.getSecond());
        return Pair.create(in,out);
    }


}
