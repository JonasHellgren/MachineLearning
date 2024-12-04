package book_rl_explained.lunar_lander.domain.agent;

import book_rl_explained.lunar_lander.domain.environment.LunarProperties;
import lombok.With;

public record AgentParameters(
        int nKernelsY,
        int nKernelsSpeed,
        double[] gammas,
        double[] gammasOneCenter,
        @With  double learningRateCritic,
        @With  double learningRateActor,
        @With double gradMeanMax,
        @With double gradStdMax
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
        return new AgentParameters(N_KERNELS_Y, N_KERNELS_SPD,
                new double[]{gamma(sigmaY), gamma(sigmaSpd)},
                new double[]{gamma(sigmaYWide), gamma(sigmaSpdWide)},
                LEARNING_RATE_CRITIC,
                LEARNING_RATE_ACTOR,
                GRAD_MEAN_MAX,
                GRAD_STD_MAX);
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
