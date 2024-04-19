package safe_rl.domain.trainers;

import lombok.Builder;
import lombok.Getter;
import policy_gradient_problems.domain.agent_interfaces.AgentParamActorTabCriticI;
import policy_gradient_problems.domain.value_classes.ProgressMeasures;
import policy_gradient_problems.environments.sink_the_ship.EnvironmentShip;
import policy_gradient_problems.environments.sink_the_ship.VariablesShip;
import safe_rl.agent_interfaces.AgentACDiscoI;
import safe_rl.domain.abstract_classes.EnvironmentI;
import safe_rl.domain.abstract_classes.StateI;
import safe_rl.domain.episode_trainers.ACDCOneStepEpisodeTrainer;
import safe_rl.domain.safety_layer.SafetyLayerI;
import safe_rl.domain.value_classes.Experience;
import safe_rl.domain.value_classes.TrainerParameters;
import safe_rl.environments.buying_electricity.AgentACDCSafeBuyer;
import safe_rl.environments.buying_electricity.StateBuying;
import safe_rl.environments.buying_electricity.VariablesBuying;
import safe_rl.helpers.EpisodeInfo;
import safe_rl.recorders.Recorders;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

//todo TrainerI
public class TrainerOneStepACDC<V> {

    EnvironmentI<V> environment;
    @Getter
    AgentACDiscoI<V> agent;
    TrainerParameters trainerParameters;
    StateI<V> startState;
    ExperienceCreator<V> experienceCreator;
    ACDCOneStepEpisodeTrainer<V> episodeTrainer;
    public Recorders recorders=new Recorders();


    @Builder
    public TrainerOneStepACDC(EnvironmentI<V> environment,
                              AgentACDiscoI<V> agent,
                              SafetyLayerI<V> safetyLayer,
                              TrainerParameters trainerParameters,
                              StateI<V> startState) {
        this.environment = environment;
        this.agent = agent;
        this.trainerParameters = trainerParameters;
        this.experienceCreator= ExperienceCreator.<V>builder()
                .environment(environment).safetyLayer(safetyLayer).parameters(trainerParameters)
                .build();
        this.startState=startState;
        this.episodeTrainer = ACDCOneStepEpisodeTrainer
                .<V>builder()
                .agent(agent).parameters(trainerParameters)
                .build();
    }

    public void train() {
        IntStream.range(0, trainerParameters.nofEpisodes()).forEach(this::processEpisode);
        printing();
    }

    private void processEpisode(int episodeIndex) {
        setStartState();
        var experiences = getExperiences();
        episodeTrainer.trainAgentFromExperiences(experiences);
        updateRecorder(experiences);
    }

    private void printing() {
        AgentACDCSafeBuyer ac=(AgentACDCSafeBuyer) agent;
        System.out.println("ac.getCritic() = " + ac.getCritic());
        System.out.println("ac.getActorMean() = " + ac.getActorMean());
    }

    public List<Experience<V>> evaluate() {
        setStartState();
        return getExperiences();
    }


    void setStartState() {
        agent.setState(startState.copy());
    }

    List<Experience<V>> getExperiences() {
        return experienceCreator.getExperiences(agent);
    }


    void updateRecorder(List<Experience<V>> experiences) {
        var ei=new EpisodeInfo<>(experiences);
        recorders.recorderTrainingProgress.add(ProgressMeasures.builder()
                        .sumRewards(ei.sumRewards())
                        .criticLoss(agent.lossCriticLastUpdate())
                        .actorLoss(agent.lossActorLastUpdate())
                        .entropy(agent.entropy())
                .build());
    }



}
