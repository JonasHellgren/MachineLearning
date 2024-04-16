package policy_gradient_problems.environments.maze;

import common.dl4j.Dl4JNetFitter;
import common.dl4j.MultiLayerNetworkCreator;
import common.dl4j.NetSettings;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import static policy_gradient_problems.environments.maze.MazeInputEncoder.manyOneHotEncodings;
import static policy_gradient_problems.environments.maze.MazeInputEncoder.oneHotEncoding;


public class NeuralActorMemoryMaze {

    MultiLayerNetwork net;
    NetSettings netSettings;
    MazeSettings mazeSettings;
    Dl4JNetFitter netFitter;

    public NeuralActorMemoryMaze(MazeSettings mazeSettings,NetSettings netSettings) {
        this.mazeSettings=mazeSettings;
        this.netSettings = netSettings;
        this.net = MultiLayerNetworkCreator.create(netSettings);
        net.init();
        this.netFitter = new Dl4JNetFitter(net, netSettings);
    }

    public void fit(double[][] inMat, double[][] outMat) {
        INDArray in = manyOneHotEncodings(inMat, mazeSettings);
        INDArray out = Nd4j.create(outMat);
        netFitter.fit(in, out);
    }


    public double[] getOutValue(double[] inData) {
        INDArray input = oneHotEncoding(inData, mazeSettings);
        INDArray reshaped = input.reshape(1, input.columns());
        return net.output(reshaped).toDoubleVector();
    }

    public double getError() {
        return netFitter.getLossLastFit();
    }



}
