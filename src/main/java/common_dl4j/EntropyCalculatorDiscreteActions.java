package common_dl4j;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.ops.transforms.Transforms;

import java.util.List;

public class EntropyCalculatorDiscreteActions implements EntropyCalculatorI {

    public static final double EPS = 1e-10;  //todo field


    public double calcEntropy(List<Double> estProb) {
        return calcEntropy(Nd4j.create(estProb),EPS);
    }

    public double calcEntropy(INDArray estProb) {
        return calcEntropy(estProb,EPS);
    }

    public double calcEntropy(INDArray estProb, double eps) {
        INDArray adjustedProbToAvoidDivZero = estProb.add(eps);
        INDArray invP = adjustedProbToAvoidDivZero.rdiv(1.0);
        INDArray surprise = Transforms.log(invP);
        INDArray vec= estProb.mul(surprise);
        return  vec.sumNumber().doubleValue();
    }


}
