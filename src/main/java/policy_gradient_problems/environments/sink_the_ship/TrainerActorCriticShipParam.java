package policy_gradient_problems.environments.sink_the_ship;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import policy_gradient_problems.domain.common_episode_trainers.ParamActorTabCriticEpisodeTrainer;
import policy_gradient_problems.domain.value_classes.TrainerParameters;

@Getter
public final class TrainerActorCriticShipParam extends TrainerAbstractShip {

    public static final double VALUE_TERMINAL_STATE = 0;

    @Builder
    public TrainerActorCriticShipParam(@NonNull EnvironmentShip environment,
                                       @NonNull AgentShipParam agent,
                                       @NonNull TrainerParameters parameters) {
        super(environment, agent, parameters);
    }

    @Override
    public void train() {
        var episodeTrainer = ParamActorTabCriticEpisodeTrainer.<VariablesShip>builder()
                .agent(agent)
                .parameters(parameters)
                .valueTermState(VALUE_TERMINAL_STATE)
                .tabularCoder((v) -> v.pos())
                .isTerminal((s) -> false)
                .build();

        for (int ei = 0; ei < parameters.nofEpisodes(); ei++) {
            agent.setRandomState();
            episodeTrainer.trainAgentFromExperiences(getExperiences(agent));
            updateTracker(agent.getCritic());
        }
    }
}
