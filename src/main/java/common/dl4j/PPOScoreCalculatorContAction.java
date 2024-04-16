package common.dl4j;

import com.google.common.base.Preconditions;
import common.math.MathUtils;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.java.Log;
import org.apache.commons.math3.util.Pair;
import org.nd4j.linalg.api.ndarray.INDArray;
import static common.other.Conditionals.executeIfTrue;

@AllArgsConstructor
@Log
public class PPOScoreCalculatorContAction implements  PPOScoreCalculatorI {
    public static final double SMALL = 1e-3;

    @NonNull Double epsilon;

    public double calcScore(INDArray label, INDArray estMeanAndStd) {
        Preconditions.checkArgument(label.rank()==1,"Rank label is not one");
        Preconditions.checkArgument(estMeanAndStd.rank()==1,"Rank estMeanAndStd is not one");
        Preconditions.checkArgument(estMeanAndStd.size(0)==2,"Length estMeanAndStd is not two");

        double action= label.getDouble(LossPPO.ACTION_INDEX);
        double adv= label.getDouble(LossPPO.ADV_INDEX);
        double pdfOld= label.getDouble(LossPPO.PROB_OLD_INDEX);
        var meanStdPair= Pair.create(estMeanAndStd.getDouble(0),estMeanAndStd.getDouble(1));
        double pdfNew=MathUtils.pdf(action,meanStdPair);


       //System.out.println("pdfOld = " + pdfOld+", meanStdPair="+meanStdPair+", pdfNew = " + pdfNew);

        double probRatio=pdfNew/Math.max(pdfOld, SMALL);
        double clippedProbRatio= MathUtils.clip(probRatio,1-epsilon,1+epsilon);
        executeIfTrue(!MathUtils.isEqualDoubles(clippedProbRatio,probRatio,SMALL),
                () -> log.fine("Prob ratio is clipped, probRatio =" + probRatio+
                        ", clippedProbRatio = " + clippedProbRatio+", pdfOld="+pdfOld));
        return MathUtils.isPos(adv)
                ? Math.min(probRatio*adv,clippedProbRatio*adv)
                : Math.max(probRatio*adv,clippedProbRatio*adv);
    }

}
