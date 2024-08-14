package safe_rl.domain.trainer.aggregates;

import lombok.AllArgsConstructor;
import safe_rl.domain.trainer.recorders.Recorder;
import safe_rl.domain.trainer.value_objects.Experience;
import safe_rl.domain.trainer.value_objects.TrainerExternal;
import safe_rl.domain.trainer.value_objects.TrainerParameters;

@AllArgsConstructor
public class Mediator<V> implements MediatorI<V> {

    TrainerExternal<V> external;
    TrainerParameters parameters;


    @Override
    public TrainerExternal<V> getExternal() {
        return null;
    }

    @Override
    public TrainerParameters getParameters() {
        return null;
    }

    @Override
    public Recorder<V> getRecorder() {
        return null;
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
