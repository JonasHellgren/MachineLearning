package policy_gradient_problems.environments.multicoin_bandit;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.java.Log;
import policy_gradient_problems.domain.common_episode_trainers.NeuralActorPPOLossTrainer;
import policy_gradient_problems.domain.value_classes.TrainerParameters;
import policy_gradient_problems.environments.twoArmedBandit.*;

@Log
@Getter
public class TrainerMultiCoinBanditAgentPPO extends TrainerAbstractMultiCoinBandit {

    public static final int NUM_IN = 1;
    public static final double LEARNING_RATE = 0.01;
    MultiCoinBanditAgentPPO agent;

    @Builder
    public TrainerMultiCoinBanditAgentPPO(EnvironmentMultiCoinBandit environment,
                                          MultiCoinBanditAgentPPO agent,
                                          TrainerParameters parameters) {
        super(environment, parameters);
        this.agent = agent;
        this.agent = new MultiCoinBanditAgentPPO(LEARNING_RATE);
    }

    @Override
    public void train() {
        NeuralActorPPOLossTrainer<VariablesBandit> episodeTrainer =
                new NeuralActorPPOLossTrainer<>(agent, parameters, EnvironmentBandit.NOF_ACTIONS);
        for (int ei = 0; ei < parameters.nofEpisodes(); ei++) {
            episodeTrainer.trainFromEpisode(super.getExperiences(agent));
            tracker.addMeasures(ei, agent.getState().getVariables().arm(), agent.getActionProbabilities());
        }
    }
}
