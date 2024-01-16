package policy_gradient_problems.the_problems.cart_pole;

import lombok.Builder;
import lombok.NonNull;
import lombok.extern.java.Log;
import policy_gradient_problems.common_episode_trainers.NeuralActorNeuralCriticEpisodeTrainer;
import policy_gradient_problems.common_generic.Experience;
import policy_gradient_problems.common_value_classes.TrainerParameters;
import java.util.List;

@Log
public class TrainerNeuralActorNeuralCriticPole extends TrainerAbstractPole {

    AgentNeuralActorNeuralCriticPole agent;
    ParametersPole parametersPole;

    public static final double VALUE_TERMINAL_STATE = 0;

    @Builder
    public TrainerNeuralActorNeuralCriticPole(@NonNull EnvironmentPole environment,
                                            @NonNull AgentNeuralActorNeuralCriticPole agent,
                                            @NonNull TrainerParameters parameters) {
        super(environment, parameters);
        this.agent = agent;
        this.parametersPole= environment.getParameters();
    }

    @Override
    public void train() {
        NeuralActorNeuralCriticEpisodeTrainer<VariablesPole> episodeTrainer =
                NeuralActorNeuralCriticEpisodeTrainer.<VariablesPole>builder()
                        .agent(agent)
                        .parameters(parameters)
                        .valueTermState(VALUE_TERMINAL_STATE)
                        .nofActions(2)
                        .build();

        for (int ei = 0; ei < parameters.nofEpisodes(); ei++) {
            agent.setState(StatePole.newAngleAndPosRandom(parametersPole));
            List<Experience<VariablesPole>> experList = getExperiences(agent);
            episodeTrainer.trainAgentFromExperiences(experList);
            updateTracker(ei,experList);

            if (experList.size() > 50) {
                    log.info("Episode = "+ei+", nof steps = " + experList.size());
            }
        }
    }
    
}
