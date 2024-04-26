package safe_rl.domain.memories;

import safe_rl.domain.value_classes.Experience;

import java.util.List;

public interface RemoveStrategyI<V> {
    void remove(List<Experience<V>> buffer);
}
