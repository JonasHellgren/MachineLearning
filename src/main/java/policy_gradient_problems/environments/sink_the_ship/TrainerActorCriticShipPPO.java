package policy_gradient_problems.environments.sink_the_ship;

import common.RandUtils;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.java.Log;
import org.jetbrains.annotations.NotNull;
import policy_gradient_problems.domain.agent_interfaces.AgentNeuralActorNeuralCriticI;
import policy_gradient_problems.domain.common_episode_trainers.EpisodeTrainerI;
import policy_gradient_problems.domain.value_classes.Experience;
import policy_gradient_problems.domain.value_classes.TrainerParameters;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log
public class TrainerActorCriticShipPPO extends TrainerAbstractShip {

    AgentNeuralActorNeuralCriticI<VariablesShip> agent;
    ShipSettings shipSettings;
    EpisodeTrainerI<VariablesShip> episodeTrainer;

    @Builder
    public TrainerActorCriticShipPPO(@NonNull EnvironmentShip environment,
                                     @NonNull AgentNeuralActorNeuralCriticI<VariablesShip> agent,
                                     @NonNull ShipSettings shipSettings,
                                     @NonNull TrainerParameters parameters,
                                     EpisodeTrainerI<VariablesShip> episodeTrainer) {
        super(environment, parameters);
        this.agent = agent;
        this.shipSettings = shipSettings;
        this.episodeTrainer=episodeTrainer;
    }
    
    @Override
    public void train() {

        for (int ei = 0; ei < parameters.nofEpisodes(); ei++) {
            System.out.println("ei = " + ei);
            setStartStateInAgent();
            var experiences = getExperiences(agent);

            //experiences.forEach(System.out::println);

            var endExp=experiences.get(experiences.size()-1);
            if ( !endExp.isTerminal()) {
                log.warning("Not ending in terminal, ei="+ei);
                //log.fine(experiences.toString());
            } else {
                episodeTrainer.trainAgentFromExperiences(experiences);
                updateTracker( getStateAngleMap(agent));
            }

        }
    }

    private void setStartStateInAgent() {
        //agent.setState(StateShip.newFromPos(RandUtils.getRandomIntNumber(0,shipSettings.nStates())));
        agent.setState(StateShip.newFromPos(0));

    }

    @NotNull
    private static Map<Integer, List<Double>> getStateAngleMap(AgentNeuralActorNeuralCriticI<VariablesShip> agent) {
        return EnvironmentShip.POSITIONS.stream()
                .collect(Collectors.toMap(
                        s -> s,
                        s -> {
                            StateShip state = StateShip.newFromPos(s);
                            var msPair = agent.meanAndStd(state);
                            double valueState = agent.criticOut(state);
                            return List.of(msPair.getFirst(), msPair.getSecond(), valueState);
                        }
                ));
    }


}

