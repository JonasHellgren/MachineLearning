package policy_gradient_problems.environments.sink_the_ship;

import common.dl4j.Dl4JNetFitter;
import common.dl4j.MultiLayerNetworkCreator;
import common.dl4j.NetSettings;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import static policy_gradient_problems.environments.sink_the_ship.ShipInputDecoder.manyOneHotEncodings;
import static policy_gradient_problems.environments.sink_the_ship.ShipInputDecoder.oneHotEncoding;

public class NeuralCriticMemoryShip {

    public static final int N_OUTPUT = 1;
    ShipSettings settings;
    MultiLayerNetwork net;
    Dl4JNetFitter fitter;

        public static NeuralCriticMemoryShip newDefault(ShipSettings settings) {
        return new NeuralCriticMemoryShip(settings,getDefaultNetSettings(settings));
    }

    public NeuralCriticMemoryShip(ShipSettings mazeSettings, NetSettings netSettings) {
        this.settings =mazeSettings;
        this.net= MultiLayerNetworkCreator.create(netSettings);
        net.init();
        this.fitter = new Dl4JNetFitter(net,netSettings);
    }

    public void fit(double[][] inMat,double[] out) {
        INDArray indMatIn = manyOneHotEncodings(inMat, settings);
        INDArray indVectorOut = Nd4j.create(out);
        fitter.fit(indMatIn,indVectorOut.reshape(out.length,N_OUTPUT));
        indVectorOut.close();
    }

    public Double getOutValue(double[] pos) {
        INDArray indArray = oneHotEncoding(pos, settings);
        return getOutValue(indArray.reshape(1, settings.nStates()));
    }

    public double getError() {
        return fitter.getLossLastFit();
    }

    private Double getOutValue(INDArray inData) {
        var output = net.output(inData, false);
        return output.getDouble();
    }


    private static NetSettings getDefaultNetSettings(ShipSettings shipSettings) {
        return NetSettings.builder()
                .nInput(shipSettings.nStates()).nHiddenLayers(1).nHidden(5)
                .nOutput(N_OUTPUT)
                .activHiddenLayer(Activation.RELU).activOutLayer(Activation.SOFTPLUS)
                .learningRate(1e-4).momentum(0.9).seed(1234)
                .lossFunction(LossFunctions.LossFunction.MSE.getILossFunction())
                //.sizeBatch(1).isNofFitsAbsolute(true).absNoFit(1)
                .sizeBatch(32).isNofFitsAbsolute(false).relativeNofFitsPerBatch(0.1)
                .weightInit(WeightInit.RELU)
                .build();
    }
    
}
