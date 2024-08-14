package safe_rl.domain.trainer.mediators;


import com.joptimizer.exception.JOptimizerException;
import safe_rl.domain.environment.aggregates.StateI;
import safe_rl.domain.environment.value_objects.Action;
import safe_rl.domain.environment.value_objects.StepReturn;
import safe_rl.domain.trainer.recorders.Recorder;
import safe_rl.domain.trainer.value_objects.Experience;
import safe_rl.domain.trainer.value_objects.MultiStepResults;
import safe_rl.domain.trainer.value_objects.TrainerExternal;
import safe_rl.domain.trainer.value_objects.TrainerParameters;

import java.util.List;

public interface MediatorBaseI<V> {
    TrainerExternal<V> getExternal();
    TrainerParameters getParameters();
    Recorder<V> getRecorder();
    StateI<V> getStartState();

    List<Experience<V>> getExperiences() throws JOptimizerException;
    void updateRecorder(List<Experience<V>> experiences);

    Action correctAction(StateI<V> state, Action action) throws JOptimizerException;
    StepReturn<V> step(StateI<V> state, Action action);
}
