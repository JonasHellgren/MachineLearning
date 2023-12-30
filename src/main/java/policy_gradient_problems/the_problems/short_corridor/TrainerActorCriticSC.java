package policy_gradient_problems.the_problems.short_corridor;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import policy_gradient_problems.common_trainers.ParamActorTabCriticEpisodeTrainer;
import policy_gradient_problems.common_value_classes.TrainerParameters;

/**
 * Explained in shortCorridor.md
 */

@Getter
public class TrainerActorCriticSC extends TrainerAbstractSC {

    AgentParamActorTabCriticSC agent;

    public static final double VALUE_TERMINAL_STATE = 0;

    @Builder
    public TrainerActorCriticSC(@NonNull EnvironmentSC environment,
                                @NonNull AgentParamActorTabCriticSC agent,
                                @NonNull TrainerParameters parameters) {
        super(environment, parameters);
        this.agent=agent;
    }


    public void train() {
        ParamActorTabCriticEpisodeTrainer<VariablesSC> episodeTrainer =
                ParamActorTabCriticEpisodeTrainer.<VariablesSC>builder()
                        .agent(agent)
                        .parameters(parameters)
                        .valueTermState(VALUE_TERMINAL_STATE)
                        .tabularCoder((v) -> v.pos())
                        .isTerminal((s) -> environment.isTerminalObserved(EnvironmentSC.getPos(s)))
                        .build();

        for (int ei = 0; ei < parameters.nofEpisodes(); ei++) {
            agent.setStateAsRandomNonTerminal();
            episodeTrainer.trainAgentFromExperiences(getExperiences(agent));
            updateTracker(ei,agent.getHelper());
        }
    }

}
