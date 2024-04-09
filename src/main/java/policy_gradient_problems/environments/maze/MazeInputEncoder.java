package policy_gradient_problems.environments.maze;

import common_dl4j.Dl4JUtil;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.stream.IntStream;

/**
 generates one-hot encoded vectors for the x and y positions separately, then concat them
 Example x=3, y=1 -> oneHotX=[0 0 0 1], oneHotY=[0 1 0] -> hotEncoding = [0 0 0 1 0 1 0]
 */

public class MazeInputEncoder {

    private MazeInputEncoder() {
    }

     static INDArray manyOneHotEncodings(double[][] inMat, MazeSettings mazeSettings) {
         int numRows = inMat.length;
         INDArray indArray = Nd4j.create(numRows, mazeSettings.nNetInputs());
         IntStream.range(0, numRows).forEach(i ->
                 indArray.putRow(i, oneHotEncoding(inMat[i], mazeSettings)));
         return indArray;
    }

    static INDArray oneHotEncoding(double[] inData, MazeSettings mazeSettings1) {
        INDArray oneHotX = Dl4JUtil.createOneHot(mazeSettings1.gridWidth(),(int) inData[0]);
        INDArray oneHotY = Dl4JUtil.createOneHot(mazeSettings1.gridHeight(),(int) inData[1]);
        return Nd4j.concat(0, oneHotX, oneHotY);
    }
}
