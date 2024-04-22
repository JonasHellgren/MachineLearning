package safe_rl.domain.trainers;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.java.Log;
import safe_rl.agent_interfaces.AgentACDiscoI;
import safe_rl.domain.abstract_classes.EnvironmentI;
import safe_rl.domain.abstract_classes.StateI;
import safe_rl.domain.episode_trainers.ACDCMultiStepEpisodeTrainer;
import safe_rl.domain.safety_layer.SafetyLayerI;
import safe_rl.domain.value_classes.TrainerParameters;
import safe_rl.helpers.ExperienceCreator;
import safe_rl.recorders.Recorders;

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
    public final Recorders<V> recorder =new Recorders<>();

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
    }

    private void processEpisode(int episodeIndex) {
        var experiences = experienceCreator.getExperiences(agent,startState.copy());
        var errorList= recorder.recorderTrainingProgress.criticLossTraj();
        episodeTrainer.trainAgentFromExperiences(experiences,errorList);
        recorder.recordTrainingProgress(experiences,agent);
    }

}
