package book_rl_explained.lunar_lander.domain.agent;

import book_rl_explained.lunar_lander.domain.environment.StateLunar;

public interface AgentI {
    double chooseAction(StateLunar state);
    double chooseActionNoExploration(StateLunar state);
    GradientMeanStd fitActor(StateLunar state, double action, double adv);
    void fitCritic(StateLunar state, double error);
    double readCritic(StateLunar state);
    AgentParameters getAgentParameters();
    MeanAndStd readActor(StateLunar state);
    GradientMeanStd gradientMeanAndLogStd(StateLunar state, double action);

}
