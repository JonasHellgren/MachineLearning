package policy_gradient_problems.the_problems.cart_pole;

import common.Dl4JUtil;
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
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NeuralMemoryPole {
    @Builder
    public record Settings(double learningRate,int nHidden) {
    }

    public static final int RAND_SEED = 12345;
    public static final double MOMENTUM = 0.9;
    static int NOF_INPUTS = 4,NOF_OUTPUTS = 1;

    MultiLayerNetwork net;
    public static final Random randGen = new Random(RAND_SEED);

    public static NeuralMemoryPole newDefault() {
        return new NeuralMemoryPole(NeuralMemoryPole.Settings
                .builder().learningRate(1e-3).nHidden(10).build());
    }

    public NeuralMemoryPole(NeuralMemoryPole.Settings settings) {
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(RAND_SEED)
                .weightInit(WeightInit.XAVIER)
                .updater(new Nesterovs(settings.learningRate, MOMENTUM))
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
    }


    public void fit(List<List<Double>> in, List<Double> out) {
        int length = in.size();
        INDArray inputNDArray = Dl4JUtil.convertListOfLists(in, NOF_INPUTS);
        INDArray outPut = Nd4j.create(ListUtils.toArray(out), length, NOF_OUTPUTS);
        DataSetIterator iterator = Dl4JUtil.getDataSetIterator(inputNDArray, outPut,randGen);
        iterator.reset();
        net.fit(iterator);
    }


    public Double getOutValue(List<Double> inData) {
        List<List<Double>> inDataList=new ArrayList<>();
        inDataList.add(inData);
        INDArray output = net.output(Dl4JUtil.convertListOfLists(inDataList, NOF_INPUTS), false);
        return output.getDouble(NOF_OUTPUTS -1);
    }




}
