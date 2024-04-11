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
                .nInput(shipSettings.nStates()).nHiddenLayers(1).nHidden(5)
                .nOutput(N_OUTPUT)
                .activHiddenLayer(Activation.RELU).activOutLayer(Activation.SOFTMAX)
                .learningRate(1e-4).momentum(0.9).seed(1234)
                .lossFunction(LossPPO.newWithEpsilonPPO(0.2))
                .sizeBatch(10).isNofFitsAbsolute(false).relativeNofFitsPerBatch(0.5)  //4 10
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
