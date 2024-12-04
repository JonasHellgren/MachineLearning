package book_rl_explained.lunar_lander.domain.agent;

import common.math.MathUtils;
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


}
