package safe_rl.domain.trainer.mediators;


import safe_rl.domain.trainer.aggregates.ReplayBufferMultiStepExp;
import safe_rl.domain.trainer.value_objects.Experience;
import safe_rl.domain.trainer.value_objects.MultiStepResults;

import java.util.List;

public interface MediatorMultiStepI<V> extends MediatorBaseI<V> {
    MultiStepResults<V> fitAgentFromNewExperiences(List<Experience<V>> experiences);
    void fitAgentFromOldExperiences(ReplayBufferMultiStepExp<V> buffer);
    void addNewExperiencesToBuffer(MultiStepResults<V> msr, ReplayBufferMultiStepExp<V> buffer);
    void maybePenalizeActionCorrection(MultiStepResults<V> msr, List<Double> lossCriticList);
}
