package safe_rl.domain.trainer.aggregates;

import com.joptimizer.exception.JOptimizerException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import safe_rl.domain.environment.aggregates.StateI;
import safe_rl.domain.trainer.recorders.Recorder;
import safe_rl.domain.trainer.value_objects.Experience;
import safe_rl.domain.trainer.value_objects.TrainerExternal;
import safe_rl.domain.trainer.value_objects.TrainerParameters;

import java.util.List;

public class Mediator<V> implements MediatorI<V> {

    @Getter  TrainerExternal<V> external;
    @Getter TrainerParameters parameters;
    @Getter Recorder<V> recorder;

    EpisodeCreator<V> episodeCreator;

    public Mediator(TrainerExternal<V> external, TrainerParameters parameters, Recorder<V> recorder) {
        this.external = external;
        this.parameters = parameters;
        this.recorder = recorder;
        this.episodeCreator=new EpisodeCreator<>(this);
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

    @Override
    public double fitAgentMemoryFromExperience(Experience<V> experience) {
        return 0;
    }*/
}
