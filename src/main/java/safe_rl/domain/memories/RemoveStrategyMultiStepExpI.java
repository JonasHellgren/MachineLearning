package safe_rl.domain.memories;

import safe_rl.domain.value_classes.ExperienceMultiStep;

import java.util.List;

public interface RemoveStrategyMultiStepExpI<V> {
    void remove(List<ExperienceMultiStep<V>> buffer);
}
