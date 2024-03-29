package policy_gradient_problems.environments.cart_pole;

import lombok.Builder;
import lombok.NonNull;
import lombok.extern.java.Log;
import policy_gradient_problems.domain.common_episode_trainers.NeuralActorNeuralCriticCrossEntropyLossTrainer;
import policy_gradient_problems.domain.value_classes.Experience;
import policy_gradient_problems.domain.value_classes.TrainerParameters;
import java.util.List;

@Log
public class TrainerNeuralActorNeuralCriticPole extends TrainerAbstractPole {

    AgentNeuralActorNeuralCriticPoleEntropyLoss agent;
    ParametersPole parametersPole;

    public static final double VALUE_TERMINAL_STATE = 0;

    @Builder
    public TrainerNeuralActorNeuralCriticPole(@NonNull EnvironmentPole environment,
                                            @NonNull AgentNeuralActorNeuralCriticPoleEntropyLoss agent,
                                            @NonNull TrainerParameters parameters) {
        super(environment, parameters);
        this.agent = agent;
        this.parametersPole= environment.getParameters();
    }

    @Override
    public void train() {
        NeuralActorNeuralCriticCrossEntropyLossTrainer<VariablesPole> episodeTrainer =
                NeuralActorNeuralCriticCrossEntropyLossTrainer.<VariablesPole>builder()
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
