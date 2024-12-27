package book_rl_explained.lunar_lander.domain.agent;

import book_rl_explained.lunar_lander.domain.environment.LunarProperties;
import lombok.Builder;
import lombok.With;
import org.hellgren.utilities.math.MyMathUtils;

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
        int batchSize,
        int nEpochs,
        double gradMeanMax,
        double gradStdMax,
        double tdMax,
        double advMax

) {

    public static final int N_KERNELS_Y = 11;
    public static final int N_KERNELS_SPD = 11;
    public static final double LEARNING_RATE_CRITIC = 1e-3;
    public static final double LEARNING_RATE_ACTOR = 1e-3;
    public static final double INIT_WEIGHT_LOG_STD = 0d;
    public static final int BATCH_SIZE = 20;
    public static final int N_EPOCHS = 5;
    public static final double REL_SIGMA = 0.9;
    public static final double GRAD_MEAN_MAX = 0.1;
    public static final double GRAD_STD_MAX = 0.01;  //small => std decreases slowly
    public static final double TD_MAX = 10d;
    public static final double ADV_MAX = 10d;

    public static AgentParameters newDefault(LunarProperties ep) {
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
                batchSize(BATCH_SIZE).
                nEpochs(N_EPOCHS).
                gradMeanMax(GRAD_MEAN_MAX).gradStdMax(GRAD_STD_MAX).
                tdMax(TD_MAX).advMax(ADV_MAX).
                build();
    }


    public double clipAdvantage(double adv0) {
        return MyMathUtils.clip(adv0, -advMax, advMax);
    }

    public double clipTdError(double e0) {
        return MyMathUtils.clip(e0, -tdMax(), tdMax());
    }


    private static double getSigmaSpdWide(double sigmaSpd, int nKernelsSpd) {
        return sigmaSpd * nKernelsSpd * 10;
    }

    private static double getSigmaYWide(double sigmaY, int nKernelsY) {
        return sigmaY * nKernelsY * 10;
    }

    private static double getSigmaSpd(LunarProperties ep, int nKernelsSpd) {
        return REL_SIGMA *(ep.spdMax() - -ep.spdMax()) / nKernelsSpd;
    }

    private static double getSigmaY(LunarProperties ep, int nKernelsY1) {
        return REL_SIGMA *(ep.yMax() - ep.ySurface()) / nKernelsY1;
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
