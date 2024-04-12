package policy_gradient_problems.environments.sink_the_ship;

import common.Conditionals;
import common.MathUtils;
import common_dl4j.Dl4JNetFitter;
import common_dl4j.MultiLayerNetworkCreator;
import common_dl4j.NetSettings;
import lombok.extern.java.Log;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.Arrays;

import static common_dl4j.LossPPO.*;
import static policy_gradient_problems.environments.sink_the_ship.ShipInputDecoder.manyOneHotEncodings;
import static policy_gradient_problems.environments.sink_the_ship.ShipInputDecoder.oneHotEncoding;

@Log
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

       // reshaped.putScalar(0, reshaped.getDouble(0)+2);
        Conditionals.executeIfTrue(reshaped.getDouble(STD_CONT)< MIN_STD,
                () -> log.fine("Small/negative standard deviation from actor - clipping"));
        reshaped.putScalar(1, MathUtils.clip(reshaped.getDouble(STD_CONT),MIN_STD, MAX_STD));
        return net.output(reshaped).toDoubleVector();
    }

    public double getError() {
        return netFitter.getLossLastFit();
    }




}
