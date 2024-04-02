package policy_gradient_problems.environments.short_corridor;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import policy_gradient_problems.domain.common_episode_trainers.ParamActorTabCriticEpisodeTrainer;
import policy_gradient_problems.domain.value_classes.TrainerParameters;

/**
 * Explained in shortCorridor.md
 */

@Getter
public final class TrainerParamActorTabCriticSC extends TrainerAbstractSC {
    public static final double VALUE_TERMINAL_STATE = 0;

    AgentParamActorTabCriticSC agent;

    @Builder
    public TrainerParamActorTabCriticSC(@NonNull EnvironmentSC environment,
                                        @NonNull AgentParamActorTabCriticSC agent,
                                        @NonNull TrainerParameters parameters) {
        super(environment, parameters);
        this.agent=agent;
    }

    @Override
    public void train() {
        var episodeTrainer = ParamActorTabCriticEpisodeTrainer
                .<VariablesSC>builder()
                .agent(agent).parameters(parameters)
                .valueTermState(VALUE_TERMINAL_STATE)
                .tabularCoder((v) -> v.pos())
                .isTerminal((s) -> environment.isTerminalObserved(EnvironmentSC.getPos(s)))
                .build();

        for (int ei = 0; ei < parameters.nofEpisodes(); ei++) {
            agent.setState(StateSC.randomNonTerminal());
            episodeTrainer.trainAgentFromExperiences(getExperiences(agent));
            updateProbRecorder(ei,(s) -> agent.getHelper().calcActionProbsInObsState(s));
        }
    }
}
