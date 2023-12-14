package common;

import lombok.Builder;
import lombok.NonNull;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.factory.Nd4j;
import java.util.List;
import java.util.Random;

@Builder
public class Dl4JNetFitter {

    @NonNull Integer nofInputs, nofOutputs;
    @NonNull MultiLayerNetwork net;
    @NonNull Random randGen;
    @NonNull NormalizerMinMaxScaler normalizerIn, normalizerOut;

    public void train(List<List<Double>> in, List<Double> out) {
        int length = in.size();
        INDArray inputNDArray = Dl4JUtil.convertListOfLists(in, nofInputs);
        INDArray outPutNDArray = Nd4j.create(ListUtils.toArray(out), length, nofOutputs);
        normalizerIn.transform(inputNDArray);
        normalizerOut.transform(outPutNDArray);
        DataSetIterator iterator = Dl4JUtil.getDataSetIterator(inputNDArray, outPutNDArray, randGen);
        iterator.reset();
        net.fit(iterator);
    }

}
