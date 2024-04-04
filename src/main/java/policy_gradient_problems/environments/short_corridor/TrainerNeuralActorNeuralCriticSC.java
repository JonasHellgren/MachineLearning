package policy_gradient_problems.environments.short_corridor;

import lombok.Builder;
import lombok.NonNull;
import policy_gradient_problems.domain.common_episode_trainers.NeuralActorNeuralCriticCEMTrainer;
import policy_gradient_problems.domain.value_classes.TrainerParameters;

public class TrainerNeuralActorNeuralCriticSC extends TrainerAbstractSC {

    AgentActorCriticSCLossCEM agent;

    public static final double VALUE_TERMINAL_STATE = 0;

    @Builder
    public TrainerNeuralActorNeuralCriticSC(@NonNull EnvironmentSC environment,
                                        @NonNull AgentActorCriticSCLossCEM agent,
                                        @NonNull TrainerParameters parameters) {
        super(environment, parameters);
        this.agent = agent;
    }

    @Override
    public void train() {
        NeuralActorNeuralCriticCEMTrainer<VariablesSC> episodeTrainer =
                NeuralActorNeuralCriticCEMTrainer.<VariablesSC>builder()
                        .agent(agent)
                        .parameters(parameters)
                        .valueTermState(VALUE_TERMINAL_STATE)
                        .nofActions(2)
                        .build();

        for (int ei = 0; ei < parameters.nofEpisodes(); ei++) {
            agent.setState(StateSC.randomNonTerminal());
            episodeTrainer.trainAgentFromExperiences(getExperiences(agent));
            updateRecorders((s) ->  agent.calcActionProbabilitiesInObsState(s),agent.lossActorAndCritic());
        }
    }
}
