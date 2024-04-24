package safe_rl.environments.buying_electricity;

import lombok.SneakyThrows;
import safe_rl.domain.abstract_classes.Action;
import safe_rl.domain.abstract_classes.OptModelI;
import safe_rl.domain.abstract_classes.StateI;

/***
 * Corrects action value if needed
 * Method correctedPower is computationally heavy so only executed when needed
 */

public class SafetyLayer<V>  {
    OptModelI model;

    public SafetyLayer(OptModelI model) {
        this.model=model;
    }

    @SneakyThrows
    public Action correctAction(StateI<V> state, Action action) {
        boolean anyViolation = isAnyViolation(state, action);
        double correctedPower= anyViolation
                ? model.correctedPower()
                : action.asDouble();
        return anyViolation
                ? Action.ofDoubleSafeCorrected(correctedPower)
                : Action.ofDouble(correctedPower);
    }

    public boolean isAnyViolation(StateI<V> state, Action action) {
        setModel((StateBuying) state, action);
        return model.isAnyViolation();
    }

    private  void setModel(StateBuying state, Action action) {
        model.setSoCAndPowerProposed(state.soc(), action.asDouble());
    }

}
