package policy_gradient_problems.environments.sink_the_ship;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import policy_gradient_problems.domain.common_episode_trainers.EpisodeTrainerI;
import policy_gradient_problems.domain.common_episode_trainers.ParamActorTabCriticEpisodeTrainer;
import policy_gradient_problems.domain.value_classes.TrainerParameters;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public final class TrainerActorCriticShipParam extends TrainerAbstractShip {

    public static final double VALUE_TERMINAL_STATE = 0;
    AgentShipParam agent;
    EpisodeTrainerI<VariablesShip> episodeTrainer;
    @Builder
    public TrainerActorCriticShipParam(@NonNull EnvironmentShip environment,
                                       @NonNull AgentShipParam agent,
                                       @NonNull TrainerParameters parameters,
                                       EpisodeTrainerI<VariablesShip> episodeTrainer) {
        super(environment, parameters);
        this.agent=agent;
        this.episodeTrainer=episodeTrainer;
    }

    @Override
    public void train() {
        AgentShipParam agentCasted= (AgentShipParam) agent;

        for (int ei = 0; ei < parameters.nofEpisodes(); ei++) {
            agentCasted.setRandomState();
            episodeTrainer.trainAgentFromExperiences(getExperiences(agent));
            Map<Integer, List<Double>> map = getStateAngleMap(agentCasted);
            updateTracker(map);
        }
    }

    @NotNull
    private static Map<Integer, List<Double>> getStateAngleMap(AgentShipParam agentCasted) {
        return EnvironmentShip.POSITIONS.stream()
                .collect(Collectors.toMap(
                        s -> s,
                        s -> {
                            var msPair = agentCasted.getMeanAndStdFromThetaVector(s);
                            double valueState = agentCasted.getCritic().getValue(s);
                            return List.of(msPair.getFirst(), msPair.getSecond(), valueState);
                        }
                ));
    }
}
