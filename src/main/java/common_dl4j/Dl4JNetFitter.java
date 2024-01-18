package common_dl4j;

import common.Conditionals;
import common.ListUtils;
import lombok.Builder;
import lombok.NonNull;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.factory.Nd4j;

import java.util.List;
import java.util.Random;

/**
 * Helper class to fit neural network from batch described by in and out
 */

@Builder
public class Dl4JNetFitter {

    @NonNull Integer nofInputs, nofOutputs;
    @NonNull MultiLayerNetwork net;
    @NonNull Random randGen;
    NormalizerMinMaxScaler normalizerIn, normalizerOut;

    public void train(List<List<Double>> in, List<Double> out) {
        train(in, out, 1);
    }

    public void train(List<List<Double>> in, List<Double> out, int nofIterations) {
        var iterator = createIterator(in, out);
        for (int i = 0; i < nofIterations; i++) {
            iterator.reset();
            net.fit(iterator);
        }
    }

    private DataSetIterator createIterator(List<List<Double>> in, List<Double> out) {
        int length = in.size();
        INDArray inputNDArray = Dl4JUtil.convertListOfLists(in, nofInputs);
        INDArray outPutNDArray = Nd4j.create(ListUtils.toArray(out), length, nofOutputs);
        //      INDArray outPutNDArray = Dl4JUtil.convertListOfLists(List.of(out), NOF_OUTPUTS);
        Conditionals.executeIfTrue(normalizerIn!=null, () -> normalizerIn.transform(inputNDArray));
        Conditionals.executeIfTrue(normalizerOut!=null,() ->  normalizerOut.transform(outPutNDArray));
        return Dl4JUtil.getDataSetIterator(inputNDArray, outPutNDArray, randGen);
    }

}
