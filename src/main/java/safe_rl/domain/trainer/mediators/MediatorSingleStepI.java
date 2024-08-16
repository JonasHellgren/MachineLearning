package safe_rl.domain.trainer.mediators;


import safe_rl.domain.trainer.value_objects.Experience;
import java.util.List;

public interface MediatorSingleStepI<V> extends MediatorBaseI<V> {
    void fitAgentFromNewExperiences(List<Experience<V>> experiences);
    void maybePenalizeActionCorrection(Experience<V> experience, List<Double> lossCriticList);
}
