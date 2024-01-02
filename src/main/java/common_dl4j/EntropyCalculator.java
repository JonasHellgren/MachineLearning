package common_dl4j;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.ops.transforms.Transforms;

public class EntropyCalculator {

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

    public static double calcEntropy(INDArray estProb) {
        return calcEntropy(estProb,EPS);
    }

    public static double calcEntropy(INDArray estProb, double eps) {
        INDArray adjustedProbToAvoidDivZero = estProb.add(eps);
        INDArray invP = adjustedProbToAvoidDivZero.rdiv(1.0);
        INDArray surprise = Transforms.log(invP);
        INDArray vec= estProb.mul(surprise);
        return  vec.sumNumber().doubleValue();
    }

}
