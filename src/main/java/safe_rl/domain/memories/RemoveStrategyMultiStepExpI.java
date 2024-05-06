package safe_rl.domain.memories;

import safe_rl.domain.value_classes.MultiStepResultItem;

import java.util.List;

public interface RemoveStrategyMultiStepExpI<V> {
    void remove(List<MultiStepResultItem<V>> buffer);
}
