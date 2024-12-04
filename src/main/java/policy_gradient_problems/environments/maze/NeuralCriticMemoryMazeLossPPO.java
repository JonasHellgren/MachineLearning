package policy_gradient_problems.environments.maze;

import common.dl4j.Dl4JNetFitter;
import common.dl4j.MultiLayerNetworkCreator;
import common.dl4j.NetSettings;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import policy_gradient_problems.domain.abstract_classes.StateI;
import static policy_gradient_problems.environments.maze.MazeInputEncoder.manyOneHotEncodings;

/***
 * nHidden important, shall not be to small
 * learningRateCritic important, shall be smaller than for the actor. Else to large entropy.
 */

public class NeuralCriticMemoryMazeLossPPO {

    public static final int N_OUTPUT = 1;
    MazeSettings mazeSettings;
    MultiLayerNetwork net;
    Dl4JNetFitter fitter;

    public static NeuralCriticMemoryMazeLossPPO newDefault(MazeSettings mazeSettings) {
        return new NeuralCriticMemoryMazeLossPPO(mazeSettings,getDefaultNetSettings(mazeSettings));
    }

    public NeuralCriticMemoryMazeLossPPO(MazeSettings mazeSettings,NetSettings netSettings) {
        this.mazeSettings=mazeSettings;
        this.net= MultiLayerNetworkCreator.create(netSettings);
        net.init();
        this.fitter = new Dl4JNetFitter(net,netSettings);
    }

    public void fit(double[][] inMat,double[] out) {
        INDArray indMatIn = manyOneHotEncodings(inMat, mazeSettings);
        INDArray indVectorOut = Nd4j.create(out);
        fitter.fit(indMatIn,indVectorOut.reshape(out.length,N_OUTPUT));
        indVectorOut.close();
    }

    public Double getOutValue(StateI<VariablesMaze> state) {
        StateMaze stateMaze=(StateMaze) state;
        INDArray indArray = MazeInputEncoder.oneHotEncoding(stateMaze.asArray(),mazeSettings);
        return getOutValue(indArray.reshape(1, mazeSettings.nNetInputs()));
    }

    public double getError() {
        return fitter.getLossLastFit();
    }

    private Double getOutValue(INDArray inData) {
        var output = net.output(inData, false);
        return output.getDouble();
    }


    private static NetSettings getDefaultNetSettings(MazeSettings mazeSettings) {
        return NetSettings.builder()
                .nInput(mazeSettings.nNetInputs()).nHiddenLayers(2).nHidden(20)
                .nOutput(N_OUTPUT)
                .activHiddenLayer(Activation.RELU).activOutLayer(Activation.IDENTITY)
                .learningRate(1e-4).momentum(0.9).seed(1234)
                .lossFunction(LossFunctions.LossFunction.MSE.getILossFunction())
                .sizeBatch(32).isNofFitsAbsolute(false).relativeNofFitsPerBatch(0.5)
                .weightInit(WeightInit.RELU)
                .build();
    }


}
