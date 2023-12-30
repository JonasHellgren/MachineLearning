package policy_gradient_problems.the_problems.sink_the_ship;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import policy_gradient_problems.common_trainers.ParamActorTabCriticEpisodeTrainer;
import policy_gradient_problems.common_value_classes.TrainerParameters;

@Getter
public class TrainerActorCriticShip extends TrainerAbstractShip {

    public static final double VALUE_TERMINAL_STATE = 0;

    @Builder
    public TrainerActorCriticShip(@NonNull EnvironmentShip environment,
                                  @NonNull AgentShip agent,
                                  @NonNull TrainerParameters parameters) {
        super(environment, agent, parameters);
    }

    public void train() {
        ParamActorTabCriticEpisodeTrainer<VariablesShip> episodeTrainer =
                ParamActorTabCriticEpisodeTrainer.<VariablesShip>builder()
                        .agent(agent)
                        .parameters(parameters)
                        .valueTermState(VALUE_TERMINAL_STATE)
                        .tabularCoder((v) -> v.pos())
                        .isTerminal((s) -> false)
                        .build();

        for (int ei = 0; ei < parameters.nofEpisodes(); ei++) {
            agent.setRandomState();
            episodeTrainer.trainAgentFromExperiences(getExperiences(agent));
            updateTracker(ei, agent.getCritic());
        }
    }
}
