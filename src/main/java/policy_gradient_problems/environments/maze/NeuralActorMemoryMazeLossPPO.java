package policy_gradient_problems.environments.maze;

import common_dl4j.LossPPO;
import common_dl4j.NetSettings;
import org.nd4j.linalg.activations.Activation;

/***
 * EpsFinDiff important
 */

public class NeuralActorMemoryMazeLossPPO {

    MazeSettings mazeSettings;
    NeuralActorMemoryMaze memory;

    public static NeuralActorMemoryMazeLossPPO newDefault(MazeSettings mazeSettings) {
        return new NeuralActorMemoryMazeLossPPO(mazeSettings, getDefaultNetSettings(mazeSettings));
    }

    public NeuralActorMemoryMazeLossPPO(MazeSettings mazeSettings, NetSettings netSettings) {
        this.mazeSettings = mazeSettings;
        this.memory = new NeuralActorMemoryMaze(mazeSettings,netSettings);
    }

    private static NetSettings getDefaultNetSettings(MazeSettings mazeSettings) {
        return NetSettings.builder()
                .nInput(mazeSettings.nNetInputs()).nHiddenLayers(2).nHidden(20)
                .nOutput(Direction.values().length)
                .activHiddenLayer(Activation.RELU).activOutLayer(Activation.SOFTMAX)
                .learningRate(1e-3).momentum(0.9).seed(1234)
                .lossFunction(LossPPO.newWithEpsPPOEpsFinDiffBetaEntropyDiscrete(0.1,1e-5,1e-3))
                //.sizeBatch(1).isNofFitsAbsolute(true).absNoFit(1)  //4 10
                .sizeBatch(32).isNofFitsAbsolute(false).relativeNofFitsPerBatch(0.5)  //4 10
                .build();
    }

    public double[] getOutValue(double[] doubles) {
        return memory.getOutValue(doubles);
    }

    public double getError() {
        return memory.getError();
    }

    public void fit(double[][] inMat, double[][] outMat) {
        memory.fit(inMat, outMat);
    }


}
