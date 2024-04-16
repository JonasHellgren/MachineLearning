package common.dl4j;

import org.nd4j.linalg.api.ndarray.INDArray;

import java.util.List;

public interface EntropyCalculatorI {
    double calcEntropy(List<Double> estProb);
    double calcEntropy(INDArray estProb);
    double calcEntropy(INDArray estProb, double eps);

}
