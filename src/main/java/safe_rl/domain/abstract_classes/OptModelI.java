package safe_rl.domain.abstract_classes;

import com.joptimizer.exception.JOptimizerException;

public interface OptModelI<V> {

    double correctedPower()  throws JOptimizerException;
    boolean isAnyViolation();
   // void setSoCAndPowerProposed(Double soc, Double powerPropose);
    void setModel(StateI<V> state0, Action action);
}
