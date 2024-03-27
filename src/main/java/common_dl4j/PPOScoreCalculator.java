package common_dl4j;

import com.google.common.base.Preconditions;
import common.MathUtils;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.java.Log;
import org.nd4j.linalg.api.ndarray.INDArray;

import static common.Conditionals.executeIfTrue;

@AllArgsConstructor
@Log
public class PPOScoreCalculator {

    public static final double SMALL = 1e-2;

    @NonNull  Double epsilon;

    public double calcScore(INDArray label, INDArray estProbabilities) {
        Preconditions.checkArgument(label.rank()==1,"Rank label is not one");
        Preconditions.checkArgument(estProbabilities.rank()==1,"Rank estProbabilities is not one");

        double action= label.getInt(0);
        double adv= label.getDouble(1);
        double probOld= label.getDouble(2);
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
