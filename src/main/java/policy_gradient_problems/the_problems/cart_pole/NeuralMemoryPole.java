package policy_gradient_problems.the_problems.cart_pole;

import common.Dl4JNetFitter;
import common.Dl4JUtil;
import common_records.NetSettings;
import org.apache.commons.math3.util.Pair;
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

    static int NOF_INPUTS = 4, NOF_OUTPUTS = 1;

    MultiLayerNetwork net;
    final Random randGen;
    NormalizerMinMaxScaler normalizerIn, normalizerOut;
    Dl4JNetFitter fitter;

    public static NeuralMemoryPole newDefault() {
        return new NeuralMemoryPole(
                NetSettings.builder().learningRate(1e-3).nHidden0(20).nHidden1(20).build(),
                ParametersPole.newDefault());
    }

    public NeuralMemoryPole(NetSettings settings, ParametersPole parameters) {
        var conf = new NeuralNetConfiguration.Builder()
                .seed(settings.seed())
                .weightInit(WeightInit.XAVIER)
                .updater(new Nesterovs(settings.learningRate(), settings.momentum()))
                .list()
                .layer(0, createHiddenLayer(settings.nHidden0()))
                .layer(1, createHiddenLayer(settings.nHidden1()))
                .layer(2, createOutLayer(settings.nHidden1()))
                .build();
        this.net = new MultiLayerNetwork(conf);
        net.init();
        this.randGen=new Random(settings.seed());
        this.normalizerIn = createNormalizerIn(parameters);
        this.normalizerOut = createNormalizerOut(parameters);
        this.fitter = Dl4JNetFitter.builder()
                .nofInputs(NOF_INPUTS).nofOutputs(NOF_OUTPUTS)
                .net(net).randGen(randGen)
                .normalizerIn(normalizerIn).normalizerOut(normalizerOut)
                .build();
    }

    public void fit(List<List<Double>> in, List<Double> out) {
        fitter.train(in, out,out.size());
    }

    public Double getOutValue(INDArray inData) {
        normalizerIn.transform(inData);
        var output = net.output(inData, false);
        normalizerOut.revertFeatures(output);
        return output.getDouble();
    }

    public Double getOutValue(List<Double> inData) {
        var inData1 = Dl4JUtil.convertList(inData, NOF_INPUTS);
        return getOutValue(inData1);
    }


    private static OutputLayer createOutLayer(Integer nHidden) {
        return new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
                .activation(Activation.IDENTITY)
                .nIn(nHidden).nOut(NOF_OUTPUTS).build();
    }

    private static DenseLayer createHiddenLayer(Integer nHidden0) {
        return new DenseLayer.Builder().nIn(NOF_INPUTS).nOut(nHidden0)
                .activation(Activation.RELU)
                .build();
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
        return Dl4JUtil.createNormalizer(inMinMax, Pair.create(0d,1d));
    }

    private static NormalizerMinMaxScaler createNormalizerOut(ParametersPole p) {
        var outMinMax = List.of(Pair.create(0d, p.maxNofSteps()));
        return Dl4JUtil.createNormalizer(outMinMax);
    }


}
