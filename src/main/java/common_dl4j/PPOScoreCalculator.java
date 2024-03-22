package common_dl4j;

import common.MathUtils;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.nd4j.linalg.api.ndarray.INDArray;

@AllArgsConstructor
public class PPOScoreCalculator {

    public static final double SMALL = 1e-5;

    @NonNull  Double epsilon;

    public double calcScore(INDArray label, INDArray estProbabilities) {
        double action= label.getInt(0,0);
        double adv= label.getDouble(0,1);
        double probOld= label.getDouble(0,2);
        double probNew= estProbabilities.getDouble(0, (int) action);
        double probRatio=probNew/Math.max(probOld, SMALL);
        double clippedProbRatio= MathUtils.clip(probRatio,1-epsilon,1+epsilon);
        //return Math.min(probRatio*adv,clippedProbRatio*adv);  //standard approach

        return MathUtils.isPos(adv)
                ? Math.min(probRatio*adv,clippedProbRatio*adv)
                : Math.max(probRatio*adv,clippedProbRatio*adv);
    }

}
