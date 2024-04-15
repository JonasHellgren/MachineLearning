package common_dl4j;

import com.google.common.base.Preconditions;
import common.math.MathUtils;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.java.Log;
import org.nd4j.linalg.api.ndarray.INDArray;

import static common.Conditionals.executeIfTrue;
import static common_dl4j.LossPPO.*;

@AllArgsConstructor
@Log
public class PPOScoreCalculatorDiscreteAction implements  PPOScoreCalculatorI {

    public static final double SMALL = 1e-2;

    @NonNull  Double epsilon;

    public double calcScore(INDArray label, INDArray estProbabilities) {
        Preconditions.checkArgument(label.rank()==1,"Rank label is not one");
        Preconditions.checkArgument(estProbabilities.rank()==1,"Rank estProbabilities is not one");

        double action= label.getInt(ACTION_INDEX);
        double adv= label.getDouble(ADV_INDEX);
        double probOld= label.getDouble(PROB_OLD_INDEX);
        double probNew= estProbabilities.getDouble((int) action);
        double probRatio=probNew/Math.max(probOld, SMALL);
        double clippedProbRatio= MathUtils.clip(probRatio,1-epsilon,1+epsilon);
        executeIfTrue(!MathUtils.isEqualDoubles(clippedProbRatio,probRatio,SMALL),
                () -> log.fine("Prob ratio is clipped, probRatio =" + probRatio+
                        ", clippedProbRatio = " + clippedProbRatio+", probOld="+probOld));
        return MathUtils.isPos(adv)
                ? Math.min(probRatio*adv,clippedProbRatio*adv)
                : Math.max(probRatio*adv,clippedProbRatio*adv);
    }

}
