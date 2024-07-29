package safe_rl.domain.agent.aggregates.remove_strategy;

import safe_rl.domain.trainer.value_objects.MultiStepResultItem;

import java.util.List;

public interface RemoveStrategyMultiStepExpI<V> {
    void remove(List<MultiStepResultItem<V>> buffer);
}
