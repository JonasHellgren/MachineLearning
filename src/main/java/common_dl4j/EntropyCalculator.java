package common_dl4j;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.ops.transforms.Transforms;

public class EntropyCalculator {

    public static double calcCrossEntropy(INDArray realProb, INDArray estProb) {
        INDArray logP = Transforms.log(estProb);
        INDArray vec= realProb.mul(logP);
        return  -vec.sumNumber().doubleValue();
    }

    public static double calcEntropy(INDArray estProb) {
        INDArray invP = estProb.rdiv(1.0);
        INDArray surprise = Transforms.log(invP);
        INDArray vec= estProb.mul(surprise);
        return  vec.sumNumber().doubleValue();
    }

}
