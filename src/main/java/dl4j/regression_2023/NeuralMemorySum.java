package dl4j.regression_2023;

import common.Dl4JNetFitter;
import lombok.Builder;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import java.util.List;
import java.util.Random;
import common.Dl4JUtil;


public class NeuralMemorySum {

    @Builder
    public record Settings(
            double learningRate,
            int nHidden) {
    }

    public static final int RAND_SEED = 12345;
    static int NOF_INPUTS = 2, NOF_OUTPUTS = 1;

    MultiLayerNetwork net;
    public static final Random randGen = new Random(RAND_SEED);
    NormalizerMinMaxScaler normalizer;
    Dl4JNetFitter fitter;

    public static NeuralMemorySum newDefault(NormalizerMinMaxScaler normalizer) {
        return new NeuralMemorySum(
                Settings.builder().learningRate(1e-3).nHidden(2).build(),
                normalizer);
    }

    public NeuralMemorySum(Settings settings, NormalizerMinMaxScaler normalizer) {
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
        this.normalizer = normalizer;
        this.fitter=Dl4JNetFitter.builder()
                .nofInputs(NOF_INPUTS).nofOutputs(NOF_OUTPUTS)
                .net(net).randGen(randGen).normalizer(normalizer)
                .build();
    }


    public void train(List<List<Double>> in, List<Double> out) {
        fitter.train(in,out);
    }

    public Double getOutValue(INDArray inData) {
        normalizer.transform(inData);
        return net.output(inData,false).getDouble();
    }

    public Double getOutValue(List<Double> inData) {
        return getOutValue(Dl4JUtil.convertList(inData, NOF_INPUTS));
    }

    public double getError() {
        return net.gradientAndScore().getSecond();
    }


}
