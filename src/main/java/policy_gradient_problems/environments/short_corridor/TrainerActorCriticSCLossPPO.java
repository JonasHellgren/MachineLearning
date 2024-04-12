package policy_gradient_problems.environments.short_corridor;

import lombok.Builder;
import lombok.NonNull;
import policy_gradient_problems.domain.agent_interfaces.AgentNeuralActorNeuralCriticI;
import policy_gradient_problems.domain.common_episode_trainers.ActorCriticEpisodeTrainerPPODiscrete;
import policy_gradient_problems.domain.value_classes.TrainerParameters;

public class TrainerActorCriticSCLossPPO extends TrainerAbstractSC{

    AgentNeuralActorNeuralCriticI<VariablesSC> agent;

    public static final double VALUE_TERMINAL_STATE = 0;

    @Builder
    public TrainerActorCriticSCLossPPO(@NonNull EnvironmentSC environment,
                                       @NonNull AgentNeuralActorNeuralCriticI<VariablesSC> agent,
                                       @NonNull TrainerParameters parameters) {
        super(environment, parameters);
        this.agent = agent;
    }

    @Override
    public void train() {
        var episodeTrainer = ActorCriticEpisodeTrainerPPODiscrete.<VariablesSC>builder()
                .agent(agent)
                .parameters(parameters)
                .valueTermState(VALUE_TERMINAL_STATE)
                .nofActions(EnvironmentSC.NOF_ACTIONS)
                .build();

        for (int ei = 0; ei < parameters.nofEpisodes(); ei++) {
            agent.setState(StateSC.randomNonTerminal());
            episodeTrainer.trainAgentFromExperiences(getExperiences(agent));
            updateRecorders((s) -> agent.actorOut(StateSC.newFromObsPos(s)), agent.lossActorAndCritic());
        }
    }
}
