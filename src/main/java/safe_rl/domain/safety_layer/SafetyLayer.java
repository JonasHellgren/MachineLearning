package safe_rl.domain.safety_layer;

import com.joptimizer.exception.JOptimizerException;
import safe_rl.domain.environment.value_objects.Action;
import safe_rl.domain.safety_layer.aggregates.OptModelI;
import safe_rl.domain.environment.aggregates.StateI;

/***
 * Corrects action value if needed
 * Method correctedPower is computationally heavy so only executed when needed
 */

public class SafetyLayer<V>  {
    OptModelI<V> model;

    public SafetyLayer(OptModelI<V> model) {
        this.model=model;
    }

    public Action correctAction(StateI<V> state, Action action) throws JOptimizerException {
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
