package safe_rl.domain.memories;

import common.other.RandUtils;
import safe_rl.domain.value_classes.ExperienceMultiStep;

import java.util.List;

public class RemoveStrategyOldestMultiStepExp<S> implements RemoveStrategyMultiStepExpI<S> {
    @Override
    public void remove(List<ExperienceMultiStep<S>> buffer) {
        buffer.remove(0);
    }
}
