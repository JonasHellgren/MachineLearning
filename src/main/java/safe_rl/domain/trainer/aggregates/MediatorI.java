package safe_rl.domain.trainer.aggregates;


import safe_rl.domain.trainer.recorders.Recorder;
import safe_rl.domain.trainer.value_objects.Experience;
import safe_rl.domain.trainer.value_objects.TrainerExternal;
import safe_rl.domain.trainer.value_objects.TrainerParameters;

public interface MediatorI<V> {
    TrainerExternal<V> getExternal();
    TrainerParameters getParameters();
    Recorder<V> getRecorder();
 //   void train();
   // Episode<V> runEpisode();
 //   double pRandomAction();
  //  double fitAgentMemoryFromExperience(Experience<V> experience);
}
