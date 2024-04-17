package safe_rl.domain.safety_layer;

import safe_rl.domain.abstract_classes.Action;
import safe_rl.domain.abstract_classes.StateI;

public interface SafetyLayerI<V> {
    Action correctAction(StateI<V> state, Action action);
}
