package safe_rl.domain.trainer.aggregates;

import com.joptimizer.exception.JOptimizerException;
import common.other.Conditionals;
import lombok.Getter;
import safe_rl.domain.environment.aggregates.StateI;
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
    ACDCMultiStepEpisodeTrainer<V> episodeFitter;
    FitterUsingReplayBuffer<V> bufferFitter;

    public Mediator(TrainerExternal<V> external,
                    TrainerParameters parameters,
                    Recorder<V> recorder,
                    int indexFeature) {
        this.external = external;
        this.parameters = parameters;
        this.recorder = recorder;
        this.episodeCreator = new EpisodeCreator<>(this);
        this.episodeFitter = new ACDCMultiStepEpisodeTrainer<>(this);
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

/*    @Override
    public void train() {

    }

    @Override
    public double pRandomAction() {
        return 0;
    }
*/

    @Override
    public MultiStepResults<V> trainAgentFromNewExperiences(List<Experience<V>> experiences) {
        var errorList = recorder.criticLossTraj();
        return episodeFitter.trainAgentFromExperiences(experiences, errorList);
    }

    @Override
    public void trainAgentFromOldExperiences(ReplayBufferMultiStepExp<V> buffer) {
        Conditionals.executeIfFalse(buffer.isEmpty(), () ->
                IntStream.range(0, parameters.nReplayBufferFitsPerEpisode())
                        .forEach(i -> bufferFitter.fit(buffer)));
    }

}
