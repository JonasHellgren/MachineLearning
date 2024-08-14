package safe_rl.domain.trainer.mediators;


import safe_rl.domain.trainer.aggregates.ReplayBufferMultiStepExp;
import safe_rl.domain.trainer.value_objects.Experience;
import safe_rl.domain.trainer.value_objects.MultiStepResults;

import java.util.List;

public interface MediatorSingleStepI<V> extends MediatorBaseI<V> {



    /*TrainerExternal<V> getExternal();
    TrainerParameters getParameters();
    Recorder<V> getRecorder();
    StateI<V> getStartState();

    List<Experience<V>> getExperiences() throws JOptimizerException;
    */
    //MultiStepResults<V> fitAgentFromNewExperiences(List<Experience<V>> experiences);
    void fitAgentFromNewExperiences(List<Experience<V>> experiences);

    //void fitAgentFromOldExperiences(ReplayBufferMultiStepExp<V> buffer);
    //void updateRecorder(List<Experience<V>> experiences);
    //void addNewExperiencesToBuffer(MultiStepResults<V> msr, ReplayBufferMultiStepExp<V> buffer);

    /*
    Action correctAction(StateI<V> state, Action action) throws JOptimizerException;
    StepReturn<V> step(StateI<V> state, Action action);*/

}
