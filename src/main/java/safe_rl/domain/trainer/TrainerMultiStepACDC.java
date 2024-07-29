package safe_rl.domain.trainer;

import com.joptimizer.exception.JOptimizerException;
import common.other.Conditionals;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.java.Log;
import safe_rl.domain.agent.interfaces.AgentACDiscoI;
import safe_rl.domain.environment.EnvironmentI;
import safe_rl.domain.environment.aggregates.StateI;
import safe_rl.domain.trainer.aggregates.ACDCMultiStepEpisodeTrainer;
import safe_rl.domain.trainer.aggregates.FitterUsingReplayBuffer;
import safe_rl.domain.trainer.aggregates.ReplayBufferMultiStepExp;
import safe_rl.domain.trainer.value_objects.Experience;
import safe_rl.domain.trainer.value_objects.TrainerParameters;
import safe_rl.domain.safety_layer.SafetyLayer;
import safe_rl.environments.trading_electricity.StateTrading;
import safe_rl.domain.simulator.AgentSimulator;
import safe_rl.domain.trainer.aggregates.EpisodeCreator;
import safe_rl.domain.trainer.recorders.Recorder;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.IntStream;


/**
 * Doing agent training
 */

@Log
public class TrainerMultiStepACDC<V> {
    EnvironmentI<V> environment;
    @Getter
    AgentACDiscoI<V> agent;
    TrainerParameters trainerParameters;
    Supplier<StateI<V>> startStateSupplier;
    EpisodeCreator<V> episodeCreator;
    ACDCMultiStepEpisodeTrainer<V> episodeTrainer;
    ReplayBufferMultiStepExp<V> buffer;
    FitterUsingReplayBuffer<V> fitter;
    @Getter
    Recorder<V> recorder;

    @Builder
    public TrainerMultiStepACDC(EnvironmentI<V> environment,
                                AgentACDiscoI<V> agent,
                                SafetyLayer<V> safetyLayer,
                                TrainerParameters trainerParameters,
                                Supplier<StateI<V>> startStateSupplier) {
        this.environment = environment;
        this.agent = agent;
        this.trainerParameters = trainerParameters;
        this.episodeCreator = EpisodeCreator.<V>builder()
                .environment(environment).safetyLayer(safetyLayer).parameters(trainerParameters)
                .build();
        this.startStateSupplier = startStateSupplier;
        this.episodeTrainer = new ACDCMultiStepEpisodeTrainer<>(agent, trainerParameters);
        buffer = ReplayBufferMultiStepExp.newFromSizeAndIsRemoveOldest(
                trainerParameters.replayBufferSize(), trainerParameters.isRemoveOldest());
        this.fitter = new FitterUsingReplayBuffer<>(agent, trainerParameters, StateTrading.INDEX_SOC);
        recorder = new Recorder<>(new AgentSimulator<>(
                agent, safetyLayer, startStateSupplier, environment));
    }

    public void train() throws JOptimizerException {
        for (int i = 0; i < trainerParameters.nofEpisodes(); i++) {
            var experiences = getExperiences();
            trainAgentFromNewExperiences(experiences);
            addNewExperienceToBuffer();
            trainAgentFromOldExperiences();
            updateRecorder(experiences);
        }
    }

    List<Experience<V>> getExperiences() throws JOptimizerException {
        return episodeCreator.getExperiences(agent, startStateSupplier.get());
    }

    void addNewExperienceToBuffer() {
        var msRes = episodeTrainer.getMultiStepResultsFromPrevFit();
        Conditionals.executeIfFalse(msRes.orElseThrow().isEmpty(), () ->
                buffer.addAll(msRes.orElseThrow().experienceList()));
    }

    void trainAgentFromOldExperiences() {
        Conditionals.executeIfFalse(buffer.isEmpty(), () ->
                IntStream.range(0, trainerParameters.nReplayBufferFitsPerEpisode())
                        .forEach(i -> fitter.fit(buffer)));
    }

    void trainAgentFromNewExperiences(List<Experience<V>> experiences) {
        var errorList = recorder.recorderTrainingProgress.criticLossTraj();
        episodeTrainer.trainAgentFromExperiences(experiences, errorList);
    }

    void updateRecorder(List<Experience<V>> experiences) {
        recorder.recordTrainingProgress(experiences, agent);
    }

}
