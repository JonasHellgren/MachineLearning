package safe_rl.domain.memories;

import safe_rl.domain.value_classes.MultiStepResultItem;

import java.util.List;

public class RemoveStrategyOldestMultiStepExp<S> implements RemoveStrategyMultiStepExpI<S> {
    @Override
    public void remove(List<MultiStepResultItem<S>> buffer) {
        buffer.remove(0);
    }
}
