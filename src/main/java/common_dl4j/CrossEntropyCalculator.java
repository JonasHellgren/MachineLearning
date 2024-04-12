package common_dl4j;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.ops.transforms.Transforms;

import java.util.List;

public class CrossEntropyCalculator {

    public static final double EPS = 1e-10;

    public static double calcCrossEntropy(INDArray realProb, INDArray estProb) {
        return calcCrossEntropy(realProb,estProb, EPS);
    }

    public static double calcCrossEntropy(INDArray realProb, INDArray estProb, double eps) {
        INDArray adjustedProbToAvoidLogZero = estProb.add(eps);
        INDArray logP = Transforms.log(adjustedProbToAvoidLogZero);
        INDArray vec= realProb.mul(logP);
        return  -vec.sumNumber().doubleValue();
    }


}
