package policy_gradient_problems.the_problems.twoArmedBandit;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.java.Log;
import policy_gradient_problems.common_trainers.NeuralActorTrainer;
import policy_gradient_problems.common_value_classes.TrainerParameters;

@Log
@Getter
public class TrainerBanditNeural extends TrainerAbstractBandit {

    public static final int NUM_IN = 1;
    AgentBanditNeural agent;

    @Builder
    public TrainerBanditNeural(EnvironmentBandit environment,
                               TrainerParameters parameters) {
        super(environment, parameters);
        this.agent = new AgentBanditNeural(parameters.learningRateActor());
    }

    public void train() {
        NeuralActorTrainer<VariablesBandit> episodeTrainer=new NeuralActorTrainer<>(agent,parameters);
        for (int ei = 0; ei < parameters.nofEpisodes(); ei++) {
            episodeTrainer.trainFromEpisode(super.getExperiences(agent));
            tracker.addMeasures(ei, agent.getState().getVariables().arm(), agent.getActionProbabilities());
        }
    }

}
