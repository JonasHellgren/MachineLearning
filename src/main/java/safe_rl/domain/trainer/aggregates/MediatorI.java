package safe_rl.domain.trainer.aggregates;


import com.joptimizer.exception.JOptimizerException;
import safe_rl.domain.environment.aggregates.StateI;
import safe_rl.domain.trainer.recorders.Recorder;
import safe_rl.domain.trainer.value_objects.Experience;
import safe_rl.domain.trainer.value_objects.MultiStepResults;
import safe_rl.domain.trainer.value_objects.TrainerExternal;
import safe_rl.domain.trainer.value_objects.TrainerParameters;

import java.util.List;

public interface MediatorI<V> {
    TrainerExternal<V> getExternal();
    TrainerParameters getParameters();
    Recorder<V> getRecorder();
    StateI<V> getStartState();

    List<Experience<V>> getExperiences() throws JOptimizerException;

    MultiStepResults<V> trainAgentFromNewExperiences(List<Experience<V>> experiences);
    void trainAgentFromOldExperiences(ReplayBufferMultiStepExp<V> buffer);



    //   void train();
   // Episode<V> runEpisode();
 //   double pRandomAction();
  //  double fitAgentMemoryFromExperience(Experience<V> experience);
}
