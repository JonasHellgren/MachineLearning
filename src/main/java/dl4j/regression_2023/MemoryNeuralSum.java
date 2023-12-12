package dl4j.regression_2023;

import common.ListUtils;
import lombok.Builder;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import common.Dl4JUtil;


public class MemoryNeuralSum {


    @Builder
    public record Settings(
            double learningRate,
            int nHidden) {
    }

    public static final int RAND_SEED = 12345;  //Random number generator seed, for reproducability
    static int NOF_INPUTS = 2,NOF_OUTPUTS = 1;

    MultiLayerNetwork net;
    public static final Random randGen = new Random(RAND_SEED);
    NormalizerMinMaxScaler normalizer;

    public static MemoryNeuralSum newDefault(NormalizerMinMaxScaler normalizer) {
        return new MemoryNeuralSum(
                Settings.builder().learningRate(1e-3).nHidden(2).build(),
                normalizer);
    }

    public MemoryNeuralSum(Settings settings,NormalizerMinMaxScaler normalizer) {
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(RAND_SEED)
                .weightInit(WeightInit.XAVIER)
                .updater(new Nesterovs(settings.learningRate, 0.9))
                .list()
                .layer(0, new DenseLayer.Builder().nIn(NOF_INPUTS).nOut(settings.nHidden)
                        .activation(Activation.RELU)
                        .build())
                .layer(1, new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
                        .activation(Activation.IDENTITY)
                        .nIn(settings.nHidden).nOut(NOF_OUTPUTS).build())
                .build();
        this.net = new MultiLayerNetwork(conf);
        net.init();
        this.normalizer=normalizer;
    }


    public void train(List<List<Double>> in, List<Double> out) {
        int length = in.size();
        INDArray inputNDArray = Dl4JUtil.convertListOfLists(in,NOF_INPUTS);
        INDArray outPut = Nd4j.create(ListUtils.toArray(out), length, NOF_OUTPUTS);
       // normalizer.transform(inputNDArray, outPut);
        DataSetIterator iterator = Dl4JUtil.getDataSetIterator(inputNDArray, outPut,randGen);
        iterator.reset();
        net.fit(iterator);
    }


    public Double getOutValue(List<Double> inData) {
        List<List<Double>> inDataList=new ArrayList<>();
        inDataList.add(inData);
        INDArray output = net.output(Dl4JUtil.convertListOfLists(inDataList,NOF_INPUTS), false);
        return output.getDouble(NOF_OUTPUTS -1);
    }



}
