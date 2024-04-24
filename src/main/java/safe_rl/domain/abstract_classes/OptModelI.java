package safe_rl.domain.abstract_classes;

import com.joptimizer.exception.JOptimizerException;

public interface OptModelI {

    double correctedPower()  throws JOptimizerException;
    boolean isAnyViolation();
    void setSoCAndPowerProposed(Double soc, Double powerPropose);
}
