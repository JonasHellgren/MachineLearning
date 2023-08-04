package multi_step_temp_diff.domain.agents.charge.input_vector_setter;

import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.environments.charge.ChargeVariables;

public interface InputVectorSetterChargeInterface<S> {
    double[] defineInArray(StateInterface<S> state);
}
