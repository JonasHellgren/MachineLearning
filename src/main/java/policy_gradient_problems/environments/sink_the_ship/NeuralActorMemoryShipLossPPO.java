package policy_gradient_problems.environments.sink_the_ship;

import common.math.MathUtils;
import common_dl4j.LossPPO;
import common_dl4j.NetSettings;
import org.nd4j.linalg.activations.Activation;

import static common_dl4j.LossPPO.*;

/**
 * activOutLayer(Activation.SOFTPLUS)  very important, ensures positive std
 * also beta entropy for ppo shall not be to large
 */

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
                .nInput(shipSettings.nStates()).nHiddenLayers(1).nHidden(10)
                .nOutput(N_OUTPUT)
                .activHiddenLayer(Activation.RELU).activOutLayer(Activation.SOFTPLUS)  //cont action <=> soft plus
                .learningRate(1e-3).momentum(0.9).seed(1234)
                .lossFunction(LossPPO.newWithEpsPPOEpsFinDiffBetaEntropyCont(0.3,1e-5,1e-3))
                .sizeBatch(32).isNofFitsAbsolute(false).relativeNofFitsPerBatch(0.5)  //4 10
                //.sizeBatch(1).isNofFitsAbsolute(true).absNoFit(1)  //4 10
                .build();
    }

    public double[] getOutValue(double[] doubles) {
        double[] outValue = memory.getOutValue(doubles);
        outValue[STD_CONT_INDEX]= MathUtils.clip(outValue[STD_CONT_INDEX],0, MAX_STD);
        return outValue;
    }

    public double getError() {
        return memory.getError();
    }

    public void fit(double[][] inMat, double[][] outMat) {
        memory.fit(inMat,outMat);
    }


}
