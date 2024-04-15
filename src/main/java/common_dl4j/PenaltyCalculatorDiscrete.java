package common_dl4j;

import org.nd4j.linalg.api.ndarray.INDArray;

public class PenaltyCalculatorDiscrete implements PenaltyCalculatorI {
    @Override
    public double penalty(INDArray estProb) {
        return 0;
    }
}
