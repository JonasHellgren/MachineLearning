package safe_rl.domain.safety_layer;

import lombok.SneakyThrows;
import safe_rl.domain.abstract_classes.Action;
import safe_rl.domain.abstract_classes.OptModelI;
import safe_rl.domain.abstract_classes.StateI;

/***
 * Corrects action value if needed
 * Method correctedPower is computationally heavy so only executed when needed
 */

public class SafetyLayer<V>  {
    OptModelI<V> model;

    public SafetyLayer(OptModelI<V> model) {
        this.model=model;
    }

    @SneakyThrows
    public Action correctAction(StateI<V> state, Action action) {
        boolean anyViolation = isAnyViolation(state, action);
        double correctedPower= anyViolation
                ? model.correctedPower(action.asDouble())
                : action.asDouble();
        return anyViolation
                ? Action.ofDoubleSafeCorrected(correctedPower)
                : Action.ofDouble(correctedPower);
    }

    public boolean isAnyViolation(StateI<V> state, Action action) {
        model.setModel(state);
        return model.isAnyViolation(action.asDouble());
    }

}
