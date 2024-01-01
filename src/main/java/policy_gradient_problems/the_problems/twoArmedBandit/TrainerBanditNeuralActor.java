package policy_gradient_problems.the_problems.twoArmedBandit;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.java.Log;
import policy_gradient_problems.common_episode_trainers.NeuralActorEpisodeTrainer;
import policy_gradient_problems.common_value_classes.TrainerParameters;

@Log
@Getter
public final class TrainerBanditNeuralActor extends TrainerAbstractBandit {

    public static final int NUM_IN = 1;
    AgentBanditNeuralActor agent;

    @Builder
    public TrainerBanditNeuralActor(EnvironmentBandit environment,
                                    TrainerParameters parameters) {
        super(environment, parameters);
        this.agent = new AgentBanditNeuralActor(parameters.learningRateActor());
    }

    @Override
    public void train() {
        NeuralActorEpisodeTrainer<VariablesBandit> episodeTrainer=
                new NeuralActorEpisodeTrainer<>(agent,parameters,EnvironmentBandit.NOF_ACTIONS);
        for (int ei = 0; ei < parameters.nofEpisodes(); ei++) {
            episodeTrainer.trainFromEpisode(super.getExperiences(agent));
            tracker.addMeasures(ei, agent.getState().getVariables().arm(), agent.getActionProbabilities());
        }
    }

}
