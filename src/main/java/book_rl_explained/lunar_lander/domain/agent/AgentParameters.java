package book_rl_explained.lunar_lander.domain.agent;

import book_rl_explained.lunar_lander.domain.environment.LunarProperties;
import com.google.common.collect.Range;
import lombok.With;

@With
public record AgentParameters(
        int nKernelsY,
        int nKernelsSpeed,
        double[] gammas,
        double[] gammasOneCenter,
        double learningRateCritic,
        double learningRateActor,
        double gradMeanMax,
        double gradStdMax,
        Range<Double> rangeMean,
        Range<Double> rangeLogStd

) {

    public static final int N_KERNELS_Y = 11;
    public static final int N_KERNELS_SPD = 11;
    public static final double LEARNING_RATE_CRITIC = 0.01;
    public static final double LEARNING_RATE_ACTOR = 0.001;
    public static final double GRAD_MEAN_MAX = 0.1;
    public static final double GRAD_STD_MAX = 0.1;

    public static AgentParameters defaultProps(LunarProperties ep) {
        double sigmaY= (ep.yMax()-ep.ySurface()) / N_KERNELS_Y;
        double sigmaSpd = (ep.spdMax() - -ep.spdMax()) / N_KERNELS_SPD;
        double sigmaYWide = sigmaY*N_KERNELS_Y*10;
        double sigmaSpdWide = sigmaSpd*N_KERNELS_SPD*10;
        double forceMaxKn = ep.forceMax() / 1000;
        return new AgentParameters(N_KERNELS_Y, N_KERNELS_SPD,
                new double[]{gamma(sigmaY), gamma(sigmaSpd)},
                new double[]{gamma(sigmaYWide), gamma(sigmaSpdWide)},
                LEARNING_RATE_CRITIC,
                LEARNING_RATE_ACTOR,
                GRAD_MEAN_MAX,
                GRAD_STD_MAX,
                Range.closed(-forceMaxKn, forceMaxKn),
                //Range.closed(-1d,1d)
                Range.closed(0.1,1d)
                );
    }

    /**
     *
     * Sigma is like the radius of a circle, controlling the size of the kernel.
     * Gamma is like the "stickiness" of the kernel, controlling how quickly it
     * decays as you move away from the center.
     */

    static double gamma (double sigma) {
        return 1 / (2 * sigma * sigma);
    }

}
