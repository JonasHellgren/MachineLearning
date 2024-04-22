package safe_rl.domain.trainers;

import common.list_arrays.ListUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.java.Log;
import policy_gradient_problems.domain.value_classes.ProgressMeasures;
import safe_rl.agent_interfaces.AgentACDiscoI;
import safe_rl.domain.abstract_classes.EnvironmentI;
import safe_rl.domain.abstract_classes.StateI;
import safe_rl.domain.episode_trainers.ACDCMultiStepEpisodeTrainer;
import safe_rl.domain.episode_trainers.ACDCOneStepEpisodeTrainer;
import safe_rl.domain.safety_layer.SafetyLayerI;
import safe_rl.domain.value_classes.Experience;
import safe_rl.domain.value_classes.TrainerParameters;
import safe_rl.environments.buying_electricity.AgentACDCSafeBuyer;
import safe_rl.helpers.EpisodeInfo;
import safe_rl.recorders.Recorders;

import java.util.List;
import java.util.stream.IntStream;

@Log
public class TrainerMultiStepACDC<V> {
    EnvironmentI<V> environment;
    @Getter
    AgentACDiscoI<V> agent;
    TrainerParameters trainerParameters;
    StateI<V> startState;
    ExperienceCreator<V> experienceCreator;
    ACDCMultiStepEpisodeTrainer<V> episodeTrainer;
    public final Recorders recorders=new Recorders();

    @Builder
    public TrainerMultiStepACDC(EnvironmentI<V> environment,
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
        this.episodeTrainer = new ACDCMultiStepEpisodeTrainer<>(agent,trainerParameters);
    }

    public void train() {
        IntStream.range(0, trainerParameters.nofEpisodes()).forEach(this::processEpisode);
        logging();
    }

    private void processEpisode(int episodeIndex) {
        var experiences = getExperiences();
        var errorList=recorders.recorderTrainingProgress.criticLossTraj();
        episodeTrainer.trainAgentFromExperiences(experiences,errorList);
        updateRecorder(experiences);
    }

    //todo in other common class
    private void logging() {
        AgentACDCSafeBuyer ac=(AgentACDCSafeBuyer) agent;
        log.info("critic() = " + ac.getCritic());
        log.info("actor mean() = " + ac.getActorMean());
        log.info("actor logStd() = " + ac.getActorLogStd());

    }

    public List<Experience<V>> evaluate() {
        return getExperiences();
    }


    List<Experience<V>> getExperiences() {
        return experienceCreator.getExperiences(agent,startState.copy());
    }

    //todo in other common class
    void updateRecorder(List<Experience<V>> experiences) {
        var ei=new EpisodeInfo<>(experiences);
        List<Double> entropies=experiences.stream()
                .map(e -> agent.entropy(e.state())).toList();

        recorders.recorderTrainingProgress.add(ProgressMeasures.builder()
                .nSteps(ei.size())
                .sumRewards(ei.sumRewards())
                .criticLoss(agent.lossCriticLastUpdate())
                .actorLoss(agent.lossActorLastUpdate())
                .entropy(ListUtils.findAverage(entropies).orElseThrow())
                .build());
    }


}
