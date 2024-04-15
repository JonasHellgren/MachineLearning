package common_dl4j;

import org.nd4j.linalg.api.ndarray.INDArray;

public interface PenaltyCalculatorI {

    double penalty(INDArray estProb);
}
