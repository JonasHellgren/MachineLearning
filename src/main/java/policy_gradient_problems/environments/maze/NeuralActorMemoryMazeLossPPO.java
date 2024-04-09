package policy_gradient_problems.environments.maze;

import common_dl4j.LossPPO;
import common_dl4j.NetSettings;
import org.nd4j.linalg.activations.Activation;

public class NeuralActorMemoryMazeLossPPO {

    MazeSettings mazeSettings;
    NeuralActorMemoryMaze memory;

    public static NeuralActorMemoryMazeLossPPO newDefault(MazeSettings mazeSettings) {
        return new NeuralActorMemoryMazeLossPPO(mazeSettings, getDefaultNetSettings(mazeSettings));
    }

    public NeuralActorMemoryMazeLossPPO(MazeSettings mazeSettings, NetSettings netSettings) {
        this.mazeSettings = mazeSettings;
        this.memory = new NeuralActorMemoryMaze(netSettings);
    }

    private static NetSettings getDefaultNetSettings(MazeSettings mazeSettings) {
        return NetSettings.builder()
                .nInput(mazeSettings.nNetInputs()).nHiddenLayers(1).nHidden(20)
                .nOutput(Direction.values().length)
                .activHiddenLayer(Activation.RELU).activOutLayer(Activation.SOFTMAX)
                .learningRate(1e-4).momentum(0.9).seed(1234)
                .lossFunction(LossPPO.newWithEpsilonPPO(0.1))
                .sizeBatch(10).isNofFitsAbsolute(false).relativeNofFitsPerBatch(3.0)  //4 10
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
