package policy_gradient_problems.environments.sink_the_ship;

import common_dl4j.Dl4JNetFitter;
import common_dl4j.Dl4JUtil;
import common_dl4j.MultiLayerNetworkCreator;
import common_dl4j.NetSettings;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import java.util.stream.IntStream;

import static policy_gradient_problems.environments.sink_the_ship.ShipInputDecoder.manyOneHotEncodings;
import static policy_gradient_problems.environments.sink_the_ship.ShipInputDecoder.oneHotEncoding;

public class NeuralActorMemoryShip {

    MultiLayerNetwork net;
    NetSettings netSettings;
    ShipSettings shipSettings;
    Dl4JNetFitter netFitter;

    public NeuralActorMemoryShip(ShipSettings shipSettings,NetSettings netSettings) {
        this.shipSettings=shipSettings;
        this.netSettings = netSettings;
        this.net = MultiLayerNetworkCreator.create(netSettings);
        net.init();
        this.netFitter = new Dl4JNetFitter(net, netSettings);
    }

    public void fit(double[][] inMat, double[][] outMat) {
        INDArray in = manyOneHotEncodings(inMat, shipSettings);
        INDArray out = Nd4j.create(outMat);
        netFitter.fit(in, out);
    }


    public double[] getOutValue(double[] inData) {
        INDArray input = oneHotEncoding(inData, shipSettings);
        INDArray reshaped = input.reshape(1, input.columns());
        return net.output(reshaped).toDoubleVector();
    }

    public double getError() {
        return netFitter.getLossLastFit();
    }




}
