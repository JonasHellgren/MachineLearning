package common.dl4j;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.List;

public class EntropyCalculatorContActions implements EntropyCalculatorI {

    public static final double DUMMY_EPS = 1e-10;

    public double calcEntropy(List<Double> estProb) {
        return calcEntropy(Nd4j.create(estProb), DUMMY_EPS);
    }

    public double calcEntropy(INDArray estProb) {
        return calcEntropy(estProb, DUMMY_EPS);
    }

    public double calcEntropy(INDArray estProb,double eps) {
        double sigma=estProb.getDouble(LossPPO.STD_CONT_INDEX);
        return entropy(sigma);
    }

    public  double entropy(double sigma) {
        return 0.5 * Math.log(2 * Math.PI * Math.E * sigma * sigma);
    }

}
