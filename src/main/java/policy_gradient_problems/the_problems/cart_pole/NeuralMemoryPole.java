package policy_gradient_problems.the_problems.cart_pole;

import common.Dl4JNetFitter;
import common.Dl4JUtil;
import lombok.Builder;
import org.apache.commons.math3.util.Pair;
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

public class NeuralMemoryPole {
    @Builder
    public record Settings(double learningRate, double momentum, int nHidden) {
    }

    public static final int RAND_SEED = 12345;
    static int NOF_INPUTS = 4, NOF_OUTPUTS = 1;

    MultiLayerNetwork net;
    public static final Random randGen = new Random(RAND_SEED);
    NormalizerMinMaxScaler normalizerIn, normalizerOut;
    Dl4JNetFitter fitter;

    public static NeuralMemoryPole newDefault() {
        return new NeuralMemoryPole(
                Settings.builder().learningRate(1e-1).nHidden(20).momentum(0.9).build(),
                ParametersPole.newDefault());
    }

    public NeuralMemoryPole(NeuralMemoryPole.Settings settings, ParametersPole parameters) {
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(RAND_SEED)
                .weightInit(WeightInit.XAVIER)
                .updater(new Nesterovs(settings.learningRate, settings.momentum))
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
        this.normalizerIn = createNormalizerIn(parameters);
        this.normalizerOut = createNormalizerOut(parameters);

        this.fitter = Dl4JNetFitter.builder()
                .nofInputs(NOF_INPUTS).nofOutputs(NOF_OUTPUTS)
                .net(net).randGen(randGen)
                .normalizerIn(normalizerIn).normalizerOut(normalizerOut)
                .build();
    }

    public NormalizerMinMaxScaler getNormalizerIn() {
        return normalizerIn;
    }

    public void fit(List<List<Double>> in, List<Double> out) {
        fitter.train(in, out);
    }


    public Double getOutValue(INDArray inData) {
        normalizerIn.transform(inData);
        INDArray output = net.output(inData, false);
        normalizerOut.revertFeatures(output);
        return output.getDouble();
    }

    public Double getOutValue(List<Double> inData) {
        INDArray inData1 = Dl4JUtil.convertList(inData, NOF_INPUTS);
        return getOutValue(inData1);
    }

    public double getError() {
        return net.gradientAndScore().getSecond();
    }


    private static NormalizerMinMaxScaler createNormalizerIn(ParametersPole p) {
        var inMinMax = List.of(
                Pair.create(-p.angleMax(), p.angleMax()),
                Pair.create(-p.xMax(), p.xMax()),
                Pair.create(-p.angleDotMax(), p.angleDotMax()),
                Pair.create(-p.xDotMax(), p.xDotMax()));
        return Dl4JUtil.createNormalizer(inMinMax, Pair.create(-1d,1d));
    }

    private static NormalizerMinMaxScaler createNormalizerOut(ParametersPole p) {
        var outMinMax = List.of(Pair.create(0d, p.maxNofSteps()));
        return Dl4JUtil.createNormalizer(outMinMax,Pair.create(-1d,1d));
    }


}
