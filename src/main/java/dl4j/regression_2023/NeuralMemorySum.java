package dl4j.regression_2023;

import common_dl4j.Dl4JNetFitter;
import common_dl4j.MultiLayerNetworkCreator;
import common_dl4j.NetSettings;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import java.util.List;
import java.util.Random;
import common_dl4j.Dl4JUtil;

/***
 * larger l2Value give slower but more stable training
 *  few neurons adequate for this simple task
 */

public class NeuralMemorySum {

    static int NOF_INPUTS = 2, NOF_OUTPUTS = 1;

    final MultiLayerNetwork net;
    final Random randGen;
    final NormalizerMinMaxScaler normalizerIn, normalizerOut;
    final Dl4JNetFitter fitter;
    final NetSettings settings;

    public static NeuralMemorySum newDefault(NormalizerMinMaxScaler normalizerIn,
                                             NormalizerMinMaxScaler normalizerOut) {

        NetSettings netSettings= NetSettings.builder()
                .nHiddenLayers(3).nInput(NOF_INPUTS).nHidden(10).nOutput(NOF_OUTPUTS)
                .activHiddenLayer(Activation.RELU).activOutLayer(Activation.IDENTITY)
                .nofFitsPerEpoch(1).l2Value(1e-2)
                .lossFunction(LossFunctions.LossFunction.MSE.getILossFunction())
                .build();
        return new NeuralMemorySum(netSettings, normalizerIn, normalizerOut);
    }

    public NeuralMemorySum(NetSettings settings,
                           NormalizerMinMaxScaler normalizerIn,
                           NormalizerMinMaxScaler normalizerOut) {
        this.settings=settings;
        this.net = MultiLayerNetworkCreator.create(settings);
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
        return getOutValue(Dl4JUtil.convertListToOneRow(inData, NOF_INPUTS));
    }

    public double getError() {
        return net.gradientAndScore().getSecond();
    }


}
