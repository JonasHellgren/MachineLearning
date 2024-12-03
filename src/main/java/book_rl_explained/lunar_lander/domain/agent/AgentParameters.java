package book_rl_explained.lunar_lander.domain.agent;

import org.apache.commons.math3.util.Pair;

public record AgentParameters(
        Pair<Double,Double> minMaxY,
        Pair<Double,Double> minMaxSpeed,
        int nKernelsY,
        int nKernelsSpeed,
        double[] gammas,
        double learningRate
) {
}
