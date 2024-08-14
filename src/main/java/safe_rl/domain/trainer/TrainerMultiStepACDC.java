package safe_rl.domain.trainer;

import com.joptimizer.exception.JOptimizerException;
import common.other.Conditionals;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.java.Log;
import safe_rl.domain.agent.interfaces.AgentACDiscoI;
import safe_rl.domain.environment.EnvironmentI;
import safe_rl.domain.environment.aggregates.StateI;
import safe_rl.domain.trainer.aggregates.*;
import safe_rl.domain.trainer.value_objects.Experience;
import safe_rl.domain.trainer.value_objects.MultiStepResults;
import safe_rl.domain.trainer.value_objects.TrainerExternal;
import safe_rl.domain.trainer.value_objects.TrainerParameters;
import safe_rl.domain.safety_layer.SafetyLayer;
import safe_rl.environments.trading_electricity.StateTrading;
import safe_rl.domain.simulator.AgentSimulator;
import safe_rl.domain.trainer.recorders.Recorder;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.IntStream;


/**
 * Doing agent training
 */

@Log
public class TrainerMultiStepACDC<V> {

    Mediator<V> mediator;

    EnvironmentI<V> environment;
    @Getter
    AgentACDiscoI<V> agent;
  //  TrainerParameters trainerParameters;
    //Supplier<StateI<V>> startStateSupplier;
   // EpisodeCreator<V> episodeCreator;
    ACDCMultiStepEpisodeTrainer<V> episodeTrainer;
    FitterUsingReplayBuffer<V> fitter;

    @Builder
    public TrainerMultiStepACDC(EnvironmentI<V> environment,
                                AgentACDiscoI<V> agent,
                                SafetyLayer<V> safetyLayer,
                                TrainerParameters trainerParameters,
                                Supplier<StateI<V>> startStateSupplier) {
        this.environment = environment;
        this.agent = agent;
    //    this.trainerParameters = trainerParameters;
        var episodeCreator = EpisodeCreator.<V>builder()
                .environment(environment).safetyLayer(safetyLayer).parameters(trainerParameters)
                .build();
      //  this.startStateSupplier = startStateSupplier;
        this.episodeTrainer = new ACDCMultiStepEpisodeTrainer<>(agent, trainerParameters);
        this.fitter = new FitterUsingReplayBuffer<>(agent, trainerParameters, StateTrading.INDEX_SOC);
        AgentSimulator<V> simulator = new AgentSimulator<>(
                agent, safetyLayer, startStateSupplier, environment);
        var recorder = new Recorder<>(simulator,trainerParameters);

        this.mediator=new Mediator<>(
                new TrainerExternal<>(environment,agent,safetyLayer,startStateSupplier),
                trainerParameters,recorder,episodeCreator);
    }


    public  Recorder<V> getRecorder() {
        return mediator.getRecorder();
    }

    public void train() throws JOptimizerException {
        ReplayBufferMultiStepExp<V> buffer = createReplayBuffer(mediator.getParameters());
        for (int i = 0; i < mediator.getParameters().nofEpisodes(); i++) {
            var experiences = mediator.getExperiences();
            var msr = trainAgentFromNewExperiences(experiences);
            addNewExperienceToBuffer(msr, buffer);
            trainAgentFromOldExperiences(buffer);
            updateRecorder(experiences);
        }
    }

    private static <V> ReplayBufferMultiStepExp<V> createReplayBuffer(TrainerParameters parameters) {
        return ReplayBufferMultiStepExp.newFromSizeAndIsRemoveOldest(
                parameters.replayBufferSize(), parameters.isRemoveOldest());
    }

/*
    List<Experience<V>> getExperiences() throws JOptimizerException {
        return episodeCreator.getExperiences(agent, mediator.getStartState());
    }
*/


    MultiStepResults<V> trainAgentFromNewExperiences(List<Experience<V>> experiences) {
        var errorList = mediator.getRecorder().criticLossTraj();
        return episodeTrainer.trainAgentFromExperiences(experiences, errorList);
    }

    void addNewExperienceToBuffer(MultiStepResults<V> msr, ReplayBufferMultiStepExp<V> buffer) {
        Conditionals.executeIfFalse(msr.isEmpty(), () ->
                buffer.addAll(msr.experienceList()));
    }

    void trainAgentFromOldExperiences(ReplayBufferMultiStepExp<V> buffer) {
        Conditionals.executeIfFalse(buffer.isEmpty(), () ->
                IntStream.range(0, mediator.getParameters().nReplayBufferFitsPerEpisode())
                        .forEach(i -> fitter.fit(buffer)));
    }

    void updateRecorder(List<Experience<V>> experiences) {
        mediator.getRecorder().recordTrainingProgress(experiences, agent);
    }

}
