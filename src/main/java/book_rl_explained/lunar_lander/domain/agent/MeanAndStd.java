package book_rl_explained.lunar_lander.domain.agent;

import org.hellgren.utilities.math.MyMathUtils;

public record MeanAndStd(
        double mean,
        double std
) {

    public static MeanAndStd of(double mean, double std) {
        return new MeanAndStd(mean, std);
    }

    public MeanAndStd createClipped(AgentParameters p) {
        return new MeanAndStd(
                MyMathUtils.clip(mean, -p.gradMeanMax(), p.gradMeanMax()),
                MyMathUtils.clip(std, -p.gradStdMax(), p.gradStdMax()));
    }


    public MeanAndStd zeroGradIfValueNotInRange(MeanAndStd grad, MeanAndStd meanAndLogStd, AgentParameters agentParameters) {
        double gradMean= (agentParameters.rangeMean().contains(meanAndLogStd.mean())) ? grad.mean() : 0.0;
        double gradStd= (agentParameters.rangeLogStd().contains(meanAndLogStd.std())) ?  grad.std(): 0.0 ;
        return new MeanAndStd(gradMean, gradStd);
    }
}
