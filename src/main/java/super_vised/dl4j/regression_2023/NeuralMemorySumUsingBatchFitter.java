package super_vised.dl4j.regression_2023;

import common.dl4j.Dl4JNetFitter;
import common.dl4j.MultiLayerNetworkCreator;
import common.dl4j.NetSettings;
import lombok.Getter;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import java.util.Random;

@Getter
public class NeuralMemorySumUsingBatchFitter {
    static int NOF_INPUTS = 2, NOF_OUTPUTS = 1;

    final MultiLayerNetwork net;
    final Random randGen;
    final Dl4JNetFitter fitter;
    final NetSettings settings;

    public static NeuralMemorySumUsingBatchFitter newDefault() {

        NetSettings netSettings= NetSettings.builder()
                .nHiddenLayers(2).nInput(NOF_INPUTS).nHidden(10).nOutput(NOF_OUTPUTS)
                .activHiddenLayer(Activation.RELU).activOutLayer(Activation.IDENTITY)
                //.nofFitsPerEpoch(1)
                .l2Value(1e-2)
                .lossFunction(LossFunctions.LossFunction.MSE.getILossFunction())
                .relativeNofFitsPerBatch(0.2).sizeBatch(16)  //used by Dl4JBatchNetFitter
                .build();
        return new NeuralMemorySumUsingBatchFitter(netSettings);
    }

    public NeuralMemorySumUsingBatchFitter(NetSettings settings) {
        this.settings=settings;
        this.net = MultiLayerNetworkCreator.create(settings);
        net.init();
        this.randGen= new Random(settings.seed());
        this.fitter=new Dl4JNetFitter(net,settings);
    }


    public void train(INDArray in, INDArray out) {
        fitter.fit(in,out);
    }

    public double getError() {
        return net.gradientAndScore().getSecond();
    }


}
