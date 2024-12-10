package book_rl_explained.lunar_lander.domain.agent;

import org.hellgren.utilities.math.MyMathUtils;

public record MeanAndStd(
        double mean,
        double std
) {

    public static MeanAndStd of(double mean, double std) {
        return new MeanAndStd(mean, std);
    }


    public MeanAndStd createStdFromLogStdDummy() {
        return new MeanAndStd(mean, std);
    }

    public MeanAndStd createStdFromLogStd() {
        return new MeanAndStd(mean, Math.exp(std));
    }

    public MeanAndStd createLogStdFromStd() {
        //g(w)=sign(derF)⋅log(∣derF∣+1):

        //return new MeanAndStd(mean, Math.max(-1,Math.log(std))); //todo fixa
        return new MeanAndStd(mean, Math.signum(std)*Math.log(Math.abs(std)+1));
    }

    public MeanAndStd createClipped(AgentParameters p) {
        return new MeanAndStd(
                MyMathUtils.clip(mean, -p.gradMeanMax(), p.gradMeanMax()),
                MyMathUtils.clip(std, -p.gradStdMax(), p.gradStdMax()));
    }


    public MeanAndStd zeroGradIfValueNotInRange(MeanAndStd grad, MeanAndStd meanAndStd, AgentParameters agentParameters) {
        double gradMean= (agentParameters.rangeMean().contains(meanAndStd.mean())) ? grad.mean() : 0.0;
        double gradStd= (agentParameters.rangeLogStd().contains(meanAndStd.std())) ?  grad.std(): 0.0 ;
        return new MeanAndStd(gradMean, gradStd);
    }
}
