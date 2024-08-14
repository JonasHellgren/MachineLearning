package safe_rl.domain.trainer.aggregates;

import com.joptimizer.exception.JOptimizerException;
import common.other.Conditionals;
import lombok.Getter;
import safe_rl.domain.environment.aggregates.StateI;
import safe_rl.domain.environment.value_objects.Action;
import safe_rl.domain.environment.value_objects.StepReturn;
import safe_rl.domain.trainer.recorders.Recorder;
import safe_rl.domain.trainer.value_objects.Experience;
import safe_rl.domain.trainer.value_objects.MultiStepResults;
import safe_rl.domain.trainer.value_objects.TrainerExternal;
import safe_rl.domain.trainer.value_objects.TrainerParameters;

import java.util.List;
import java.util.stream.IntStream;

public class Mediator<V> implements MediatorI<V> {

    @Getter
    TrainerExternal<V> external;
    @Getter
    TrainerParameters parameters;
    @Getter
    Recorder<V> recorder;

    EpisodeCreator<V> episodeCreator;
    ACDCMultiStepEpisodeFitter<V> episodeFitter;
    FitterUsingReplayBuffer<V> bufferFitter;

    public Mediator(TrainerExternal<V> external,
                    TrainerParameters parameters,
                    Recorder<V> recorder,
                    int indexFeature) {
        this.external = external;
        this.parameters = parameters;
        this.recorder = recorder;
        this.episodeCreator = new EpisodeCreator<>(this);
        this.episodeFitter = new ACDCMultiStepEpisodeFitter<>(this);
        this.bufferFitter = new FitterUsingReplayBuffer<>(this,indexFeature);
    }


    @Override
    public StateI<V> getStartState() {
        return external.startStateSupplier().get();
    }

    @Override
    public List<Experience<V>> getExperiences() throws JOptimizerException {
        return episodeCreator.getExperiences(external.agent(), getStartState());
    }

    @Override
    public MultiStepResults<V> fitAgentFromNewExperiences(List<Experience<V>> experiences) {
        var errorList = recorder.criticLossTraj();
        return episodeFitter.trainAgentFromExperiences(experiences, errorList);
    }

    @Override
    public void fitAgentFromOldExperiences(ReplayBufferMultiStepExp<V> buffer) {
        Conditionals.executeIfFalse(buffer.isEmpty(), () ->
                IntStream.range(0, parameters.nReplayBufferFitsPerEpisode())
                        .forEach(i -> bufferFitter.fit(buffer)));
    }

    @Override
    public void updateRecorder(List<Experience<V>> experiences) {
        recorder.recordTrainingProgress(experiences, external.agent());
    }

    @Override
    public void addNewExperiencesToBuffer(MultiStepResults<V> msr, ReplayBufferMultiStepExp<V> buffer) {
        Conditionals.executeIfFalse(msr.isEmpty(), () ->
                buffer.addAll(msr.experienceList()));
    }

    @Override
    public Action correctAction(StateI<V> state, Action action) throws JOptimizerException {
        return getExternal().safetyLayer().correctAction(state, action);
    }

    @Override
    public StepReturn<V> step(StateI<V> state, Action action) {
        return external.environment().step(state, action);
    }

}
