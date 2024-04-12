package policy_gradient_problems.environments.sink_the_ship;

import common_dl4j.LossPPO;
import common_dl4j.NetSettings;
import org.nd4j.linalg.activations.Activation;

public class NeuralActorMemoryShipLossPPO {

    public static final int N_OUTPUT = 2;
    NeuralActorMemoryShip memory;

    public static NeuralActorMemoryShipLossPPO newDefault(ShipSettings shipSettings) {
        return new NeuralActorMemoryShipLossPPO(
                ShipSettings.newDefault(),
                getDefaultNetSettings(shipSettings));
    }

    public NeuralActorMemoryShipLossPPO(ShipSettings shipSettings,NetSettings netSettings) {
        this.memory =new NeuralActorMemoryShip(shipSettings,netSettings);
    }

    private static NetSettings getDefaultNetSettings(ShipSettings shipSettings) {
        return NetSettings.builder()
                .nInput(shipSettings.nStates()).nHiddenLayers(1).nHidden(20)
                .nOutput(N_OUTPUT)
                .activHiddenLayer(Activation.RELU).activOutLayer(Activation.IDENTITY)  //cont action <=> identity
                .learningRate(1e-4).momentum(0.9).seed(1234)
                .lossFunction(LossPPO.newWithEpsPPOEpsFinDiffBetaEntropyCont(0.1,1e-5,1e-1))
                .sizeBatch(32).isNofFitsAbsolute(false).relativeNofFitsPerBatch(0.5)  //4 10
                //.sizeBatch(1).isNofFitsAbsolute(true).absNoFit(1)  //4 10
                .build();
    }

    public double[] getOutValue(double[] doubles) {
        return memory.getOutValue(doubles);
    }

    public double getError() {
        return memory.getError();
    }

    public void fit(double[][] inMat, double[][] outMat) {
        memory.fit(inMat,outMat);
    }


}
