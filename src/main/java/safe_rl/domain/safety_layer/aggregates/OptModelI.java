package safe_rl.domain.safety_layer.aggregates;

import com.joptimizer.exception.JOptimizerException;
import lombok.NonNull;
import safe_rl.domain.environment.aggregates.StateI;

public interface OptModelI<V> {
    double correctedPower(@NonNull Double power)  throws JOptimizerException;
    boolean isAnyViolation(@NonNull Double power);
    void setModel(StateI<V> state0);
}
