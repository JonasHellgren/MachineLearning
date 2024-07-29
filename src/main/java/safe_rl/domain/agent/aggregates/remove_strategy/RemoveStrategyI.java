package safe_rl.domain.agent.aggregates.remove_strategy;

import safe_rl.domain.trainer.value_objects.Experience;

import java.util.List;

public interface RemoveStrategyI<V> {
    void remove(List<Experience<V>> buffer);
}
