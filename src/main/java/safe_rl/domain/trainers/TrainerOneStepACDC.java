package safe_rl.domain.trainers;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.java.Log;
import safe_rl.agent_interfaces.AgentACDiscoI;
import safe_rl.domain.abstract_classes.EnvironmentI;
import safe_rl.domain.abstract_classes.StateI;
import safe_rl.domain.episode_trainers.ACDCOneStepEpisodeTrainer;
import safe_rl.domain.value_classes.Experience;
import safe_rl.domain.value_classes.TrainerParameters;
import safe_rl.domain.safety_layer.SafetyLayer;
import safe_rl.helpers.AgentSimulator;
import safe_rl.helpers.ExperienceCreator;
import safe_rl.recorders.Recorders;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.IntStream;

//todo TrainerI
@Log
public class TrainerOneStepACDC<V> {

    EnvironmentI<V> environment;
    @Getter AgentACDiscoI<V> agent;
    TrainerParameters trainerParameters;
    Supplier<StateI<V>> startStateSupplier;
    ExperienceCreator<V> experienceCreator;
    ACDCOneStepEpisodeTrainer<V> episodeTrainer;
    @Getter Recorders<V> recorder;

    @Builder
    public TrainerOneStepACDC(EnvironmentI<V> environment,
                              AgentACDiscoI<V> agent,
                              SafetyLayer<V> safetyLayer,
                              TrainerParameters trainerParameters,
                              Supplier<StateI<V>> startStateSupplier) {
        this.environment = environment;
        this.agent = agent;
        this.trainerParameters = trainerParameters;
        this.experienceCreator= ExperienceCreator.<V>builder()
                .environment(environment).safetyLayer(safetyLayer).parameters(trainerParameters)
                .build();
        this.startStateSupplier=startStateSupplier;
        this.episodeTrainer = ACDCOneStepEpisodeTrainer
                .<V>builder()
                .agent(agent).parameters(trainerParameters)
                .build();
        recorder=new Recorders<>(new AgentSimulator<>(
                agent,safetyLayer,startStateSupplier,environment));
    }

    public void train() {
        IntStream.range(0, trainerParameters.nofEpisodes()).forEach(this::processEpisode);
    }

    public List<Experience<V>> evaluate() {
        return experienceCreator.getExperiences(agent,startStateSupplier.get());
    }

    private void processEpisode(int episodeIndex) {
        var experiences = evaluate();
        var errorList= recorder.recorderTrainingProgress.criticLossTraj();
        episodeTrainer.trainAgentFromExperiences(experiences,errorList);
        recorder.recordTrainingProgress(experiences,agent);
    }

}
