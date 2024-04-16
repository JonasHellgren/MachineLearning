package common.dl4j;

import org.nd4j.linalg.api.ndarray.INDArray;

public interface PPOScoreCalculatorI {

    double calcScore(INDArray label, INDArray estMeanAndStd);
}
