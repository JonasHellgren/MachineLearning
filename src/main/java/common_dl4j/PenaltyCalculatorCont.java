package common_dl4j;

import lombok.AllArgsConstructor;
import org.nd4j.linalg.api.ndarray.INDArray;

import static common_dl4j.LossPPO.STD_CONT_INDEX;

/***
 * WHen below theshold, penaly starts to increase towards maxPen
 */

@AllArgsConstructor
public class PenaltyCalculatorCont implements PenaltyCalculatorI{

    static final double THRESHOLD_DEF = common_dl4j.LossPPO.MIN_STD;
    static final double MAX_PEN = 100;
    static final double STEEPNESS = 1000;

    double threshold;
    double maxPen;

    public static PenaltyCalculatorCont newDefault() {
        return new PenaltyCalculatorCont(THRESHOLD_DEF,MAX_PEN);
    }

    @Override
    public double penalty(INDArray estProb) {
        double sigma=estProb.getDouble(STD_CONT_INDEX);
         return MAX_PEN / (1 + Math.exp(STEEPNESS * (sigma - threshold)));
    }
}
