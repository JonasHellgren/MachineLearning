package book_rl_explained.lunar_lander.domain.agent;

import book_rl_explained.lunar_lander.domain.environment.LunarProperties;
import lombok.Builder;
import lombok.With;

@With
@Builder
public record AgentParameters(
        int nKernelsY,
        int nKernelsSpeed,
        double[] gammas,
        double[] gammasOneCenter,
        double initWeightLogStd,
        double learningRateCritic,
        double learningRateActor,
        double gradMeanMax,
        double gradStdMax

) {

    public static final int N_KERNELS_Y = 11;
    public static final int N_KERNELS_SPD = 11;
    public static final double LEARNING_RATE_CRITIC = 0.1;
    public static final double LEARNING_RATE_ACTOR = 0.01;
    public static final double GRAD_MEAN_MAX = 0.1;
    public static final double GRAD_STD_MAX = 0.01;
    public static final double INIT_WEIGHT_LOG_STD = 0d;

    public static AgentParameters defaultParams(LunarProperties ep) {
        return of(ep, N_KERNELS_Y, N_KERNELS_SPD);
    }

    public static AgentParameters of(LunarProperties ep, int nKernelsY, int nKernelsSpd) {
        double sigmaY = getSigmaY(ep, nKernelsY);
        double sigmaSpd = getSigmaSpd(ep, nKernelsSpd);
        return AgentParameters.builder().
                nKernelsY(nKernelsY).
                nKernelsSpeed(nKernelsSpd).
                gammas(new double[]{
                        gamma(sigmaY),
                        gamma(sigmaSpd)}).
                gammasOneCenter(new double[]{
                        gamma(getSigmaYWide(sigmaY, nKernelsY)),
                        gamma(getSigmaSpdWide(sigmaSpd, nKernelsSpd))}).
                initWeightLogStd(INIT_WEIGHT_LOG_STD).
                learningRateCritic(LEARNING_RATE_CRITIC).
                learningRateActor(LEARNING_RATE_ACTOR).
                gradMeanMax(GRAD_MEAN_MAX).
                gradStdMax(GRAD_STD_MAX).
                build();
    }


    private static double getSigmaSpdWide(double sigmaSpd, int nKernelsSpd) {
        return sigmaSpd * nKernelsSpd * 10;
    }

    private static double getSigmaYWide(double sigmaY, int nKernelsY) {
        return sigmaY * nKernelsY * 10;
    }

    private static double getSigmaSpd(LunarProperties ep, int nKernelsSpd) {
        return (ep.spdMax() - -ep.spdMax()) / nKernelsSpd;
    }

    private static double getSigmaY(LunarProperties ep, int nKernelsY1) {
        return (ep.yMax() - ep.ySurface()) / nKernelsY1;
    }

    /**
     * Sigma is like the radius of a circle, controlling the size of the kernel.
     * Gamma is like the "stickiness" of the kernel, controlling how quickly it
     * decays as you move away from the center.
     */

    static double gamma(double sigma) {
        return 1 / (2 * sigma * sigma);
    }

}
