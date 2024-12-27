package book_rl_explained.lunar_lander.domain.agent;

import book_rl_explained.lunar_lander.domain.environment.StateLunar;
import book_rl_explained.radial_basis.TrainData;

public interface AgentI {
    double chooseAction(StateLunar state);
    double chooseActionNoExploration(StateLunar state);
    void fitCritic(TrainData data);
    double readCritic(StateLunar state);
    AgentParameters getAgentParameters();
    MeanStd readActor(StateLunar state);
    GradientMeanStd gradientMeanAndLogStd(StateLunar state, double action);
    void fitActor(TrainData dataMean, TrainData dataStd);
    void copyActivationsInCriticToActorMean();
}
