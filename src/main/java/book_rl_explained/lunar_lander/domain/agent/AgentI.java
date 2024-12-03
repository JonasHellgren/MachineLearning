package book_rl_explained.lunar_lander.domain.agent;

import book_rl_explained.lunar_lander.domain.environment.StateLunar;
import org.apache.commons.math3.util.Pair;

public interface AgentI {
    double chooseAction(StateLunar state);
    Pair<Double,Double> fitActor(StateLunar state, double action, double adv);
    void fitCritic(StateLunar state, double error);
    double readCritic(StateLunar state);
    Pair<Double, Double> gradientMeanAndStd(StateLunar state, double action);

}
