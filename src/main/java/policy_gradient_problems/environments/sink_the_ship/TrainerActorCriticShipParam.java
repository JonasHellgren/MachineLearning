package policy_gradient_problems.environments.sink_the_ship;

import common.other.RandUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import policy_gradient_problems.domain.agent_interfaces.AgentParamActorTabCriticI;
import policy_gradient_problems.domain.common_episode_trainers.EpisodeTrainerI;
import policy_gradient_problems.domain.value_classes.TrainerParameters;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public final class TrainerActorCriticShipParam extends TrainerAbstractShip {

    public static final double VALUE_TERMINAL_STATE = 0;
    AgentParamActorTabCriticI<VariablesShip> agent;
    EpisodeTrainerI<VariablesShip> episodeTrainer;
    @Builder
    public TrainerActorCriticShipParam(@NonNull EnvironmentShip environment,
                                       @NonNull AgentParamActorTabCriticI<VariablesShip> agent,
                                       @NonNull TrainerParameters parameters,
                                       EpisodeTrainerI<VariablesShip> episodeTrainer) {
        super(environment, parameters);
        this.agent=agent;
        this.episodeTrainer=episodeTrainer;
    }

    @Override
    public void train() {
        for (int ei = 0; ei < parameters.nofEpisodes(); ei++) {
            agent.setState(StateShip.newFromPos(RandUtils.getRandomIntNumber(0,EnvironmentShip.nofStates())));
            episodeTrainer.trainAgentFromExperiences(getExperiences(agent));
            Map<Integer, List<Double>> map = getStateAngleMap(agent);
            updateTracker(map);
        }
    }

    @NotNull
    private static Map<Integer, List<Double>> getStateAngleMap(AgentParamActorTabCriticI<VariablesShip> agent) {
        return EnvironmentShip.POSITIONS.stream()
                .collect(Collectors.toMap(
                        s -> s,
                        s -> {
                            var msPair = agent.meanAndStd(s);
                            double valueState = agent.getCriticValue(s);
                            return List.of(msPair.getFirst(), msPair.getSecond(), valueState);
                        }
                ));
    }
}
