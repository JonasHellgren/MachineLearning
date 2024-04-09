package policy_gradient_problems.environments.cart_pole;

import common_dl4j.*;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import policy_gradient_problems.environments.short_corridor.EnvironmentSC;
import java.util.List;
import static common.ListUtils.arrayPrimitiveDoublesToList;

/**
  State, observed position, is transformed to one hot representation, vector with zeros and one 1-element
  The 1-element is placed at same index as observed position
 */

public class NeuralActorMemorySC {

    public static int NOF_INPUTS = 3, NOF_OUTPUTS = EnvironmentSC.NOF_ACTIONS;

    MultiLayerNetwork net;
    NetSettings netSettings;
    Dl4JNetFitter netFitter;

    public NeuralActorMemorySC(NetSettings netSettings) {
        this.netSettings = netSettings;
        this.net = MultiLayerNetworkCreator.create(netSettings);
        net.init();
        this.netFitter = new Dl4JNetFitter(net, netSettings);
    }

    public void fit(List<List<Double>> inList, List<List<Double>> outList) {
        INDArray in = transformDiscretePosState(inList);
        INDArray out = Dl4JUtil.convertListOfLists(outList);
        netFitter.fit(in, out);
    }

    public double[] getOutValue(double[] inData) {
        INDArray indArray = getOneHot(List.of(arrayPrimitiveDoublesToList(inData)), 0).reshape(1, NOF_INPUTS);
        return net.output(indArray).toDoubleVector();
    }

    public double getError() {
        return net.gradientAndScore().getSecond();
    }

    private INDArray transformDiscretePosState(List<List<Double>> inList) {
        int nofPoints = inList.size();
        INDArray inArr = Nd4j.create(nofPoints, NOF_INPUTS);
        for (int i = 0; i < nofPoints; i++) {
            inArr.getRow(i).addi(getOneHot(inList, i));
        }
        return inArr;
    }

    private static INDArray getOneHot(List<List<Double>> inList, int i) {
        return Dl4JUtil.createOneHotOld(NOF_INPUTS, inList.get(i).get(0).intValue());
    }


}
