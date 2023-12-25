package dl4j.regression_2023;

import common_dl4j.Dl4JNetFitter;
import common_dl4j.NetSettings;
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
import common_dl4j.Dl4JUtil;


public class NeuralMemorySum {

    static int NOF_INPUTS = 2, NOF_OUTPUTS = 1;

    final MultiLayerNetwork net;
    final Random randGen;
    final NormalizerMinMaxScaler normalizerIn, normalizerOut;
    final Dl4JNetFitter fitter;
    final NetSettings settings;

    public static NeuralMemorySum newDefault(NormalizerMinMaxScaler normalizerIn,
                                             NormalizerMinMaxScaler normalizerOut) {
        return new NeuralMemorySum(NetSettings.newDefault(), normalizerIn, normalizerOut);
    }

    public NeuralMemorySum(NetSettings settings,
                           NormalizerMinMaxScaler normalizerIn,
                           NormalizerMinMaxScaler normalizerOut) {
        this.settings=settings;
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(settings.seed())
                .weightInit(WeightInit.XAVIER)
                .updater(new Nesterovs(settings.learningRate(), settings.momentum()))
                .list()
                .layer(0, new DenseLayer.Builder().nIn(NOF_INPUTS).nOut(settings.nHidden())
                        .activation(Activation.RELU)
                        .build())
                .layer(1, new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
                        .activation(Activation.IDENTITY)
                        .nIn(settings.nHidden()).nOut(NOF_OUTPUTS).build())
                .build();
        this.net = new MultiLayerNetwork(conf);
        net.init();
        this.randGen= new Random(settings.seed());
        this.normalizerIn = normalizerIn;
        this.normalizerOut = normalizerOut;
        this.fitter=Dl4JNetFitter.builder()
                .nofInputs(NOF_INPUTS).nofOutputs(NOF_OUTPUTS)
                .net(net).randGen(randGen)
                .normalizerIn(normalizerIn).normalizerOut(normalizerOut)
                .build();
    }


    public void train(List<List<Double>> in, List<Double> out) {
        fitter.train(in,out,settings.nofFitsPerEpoch());
    }

    public Double getOutValue(INDArray inData) {
        normalizerIn.transform(inData);
        INDArray output = net.output(inData, false);
        normalizerOut.revertFeatures(output);
        return output.getDouble();
    }

    public Double getOutValue(List<Double> inData) {
        return getOutValue(Dl4JUtil.convertList(inData, NOF_INPUTS));
    }

    public double getError() {
        return net.gradientAndScore().getSecond();
    }


}
