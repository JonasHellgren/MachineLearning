package policy_gradient_problems.environments.sink_the_ship;

import common_dl4j.Dl4JUtil;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.stream.IntStream;

public class ShipInputDecoder {

    static INDArray manyOneHotEncodings(double[][] inMat, ShipSettings shipSettings) {
        int numRows = inMat.length;
        INDArray indArray = Nd4j.create(numRows, shipSettings.nStates());
        IntStream.range(0, numRows).forEach(i ->
                indArray.putRow(i, oneHotEncoding(inMat[i], shipSettings)));
        return indArray;
    }

    static INDArray oneHotEncoding(double[] inData, ShipSettings shipSettings) {
        return Dl4JUtil.createOneHot(shipSettings.nStates(),(int) inData[0]);
    }

}
