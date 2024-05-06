package safe_rl.domain.abstract_classes;

import com.joptimizer.exception.JOptimizerException;
import lombok.NonNull;

public interface OptModelI<V> {

    double correctedPower(@NonNull Double power)  throws JOptimizerException;
    boolean isAnyViolation(@NonNull Double power);
    void setModel(StateI<V> state0);
}
