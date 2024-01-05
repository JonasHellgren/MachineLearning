package policy_gradient_problems.the_problems.short_corridor;

import lombok.Builder;
import lombok.NonNull;
import policy_gradient_problems.common_episode_trainers.NeuralActorNeuralCriticEpisodeTrainer;
import policy_gradient_problems.common_value_classes.TrainerParameters;

public class TrainerNeuralActorNeuralCriticSC extends TrainerAbstractSC {

    AgentNeuralActorNeuralCriticSC agent;

    public static final double VALUE_TERMINAL_STATE = 0;

    @Builder
    public TrainerNeuralActorNeuralCriticSC(@NonNull EnvironmentSC environment,
                                        @NonNull AgentNeuralActorNeuralCriticSC agent,
                                        @NonNull TrainerParameters parameters) {
        super(environment, parameters);
        this.agent = agent;
    }

    @Override
    public void train() {
        NeuralActorNeuralCriticEpisodeTrainer<VariablesSC> episodeTrainer =
                NeuralActorNeuralCriticEpisodeTrainer.<VariablesSC>builder()
                        .agent(agent)
                        .parameters(parameters)
                        .valueTermState(VALUE_TERMINAL_STATE)
                        .nofActions(2)
                        .build();

        for (int ei = 0; ei < parameters.nofEpisodes(); ei++) {
            agent.setState(StateSC.randomNonTerminal());
            episodeTrainer.trainAgentFromExperiences(getExperiences(agent));
            updateTracker(ei,(s) ->  agent.calcActionProbabilitiesInObsState(s));
        }
    }
}
