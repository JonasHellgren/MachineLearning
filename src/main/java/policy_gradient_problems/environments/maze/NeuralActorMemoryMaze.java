package policy_gradient_problems.environments.maze;

import common_dl4j.Dl4JNetFitter;
import common_dl4j.Dl4JUtil;
import common_dl4j.MultiLayerNetworkCreator;
import common_dl4j.NetSettings;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;


/**
 generates one-hot encoded vectors for the x and y positions separately, then concat them
  Example x=3, y=1 -> oneHotX=[0 0 0 1], oneHotY=[0 1 0] -> hotEncoding = [0 0 0 1 0 1 0]
 */

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
        INDArray in = getManyOneHot(inMat);
        INDArray out = Nd4j.create(outMat);
        netFitter.fit(in, out);
    }


    public double[] getOutValue(double[] inData) {
        INDArray input = hotEncoding(inData);
        INDArray reshaped = input.reshape(1, input.columns());
        return net.output(reshaped).toDoubleVector();
    }


    private INDArray getManyOneHot(double[][] inMat) {
        int numRows=inMat.length;
        int numColumns=mazeSettings.nNetInputs();
        INDArray indArray=  Nd4j.create(numRows, numColumns);
        for (int i = 0; i < numRows ; i++) {
            indArray.putRow(i,hotEncoding(inMat[i]));
        }
        return indArray;
    }

    public double getError() {
        return net.gradientAndScore().getSecond();
    }

    private INDArray hotEncoding(double[] inData) {
        INDArray oneHotX = Dl4JUtil.createOneHot(mazeSettings.gridWidth(),(int) inData[0]);
        INDArray oneHotY = Dl4JUtil.createOneHot(mazeSettings.gridHeight(),(int) inData[1]);
        return Nd4j.concat(0, oneHotX, oneHotY);
    }


}
