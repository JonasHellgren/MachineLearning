package safe_rl.domain.trainer.mediators;

import com.joptimizer.exception.JOptimizerException;
import lombok.Getter;
import safe_rl.domain.environment.aggregates.StateI;
import safe_rl.domain.environment.value_objects.Action;
import safe_rl.domain.environment.value_objects.StepReturn;
import safe_rl.domain.trainer.aggregates.ACDCOneStepEpisodeFitter;
import safe_rl.domain.trainer.aggregates.EpisodeCreator;
import safe_rl.domain.trainer.recorders.Recorder;
import safe_rl.domain.trainer.value_objects.Experience;
import safe_rl.domain.trainer.value_objects.TrainerExternal;
import safe_rl.domain.trainer.value_objects.TrainerParameters;

import java.util.List;

/**
 * See comment in MediatorMultiStep
 * @param <V>
 */

public class MediatorSingleStep<V> implements MediatorSingleStepI<V> {
    @Getter
    TrainerExternal<V> external;
    @Getter
    TrainerParameters parameters;
    @Getter
    Recorder<V> recorder;

    EpisodeCreator<V> episodeCreator;
    ACDCOneStepEpisodeFitter<V> episodeFitter;

    public MediatorSingleStep(TrainerExternal<V> external,
                              TrainerParameters parameters,
                              Recorder<V> recorder) {
        this.external = external;
        this.parameters = parameters;
        this.recorder = recorder;
        this.episodeCreator = new EpisodeCreator<>(this);
        this.episodeFitter = new ACDCOneStepEpisodeFitter<>(this);
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
    public void updateRecorder(List<Experience<V>> experiences) {
        recorder.recordTrainingProgress(experiences, external.agent());
    }

    @Override
    public Action correctAction(StateI<V> state, Action action) throws JOptimizerException {
        return external.safetyLayer().correctAction(state, action);
    }

    @Override
    public StepReturn<V> step(StateI<V> state, Action action) {
        return external.environment().step(state, action);
    }

    @Override
    public void fitAgentFromNewExperiences(List<Experience<V>> experiences) {
        var errorList= recorder.criticLossTraj();
        episodeFitter.trainAgentFromExperiences(experiences,errorList);
    }
}
