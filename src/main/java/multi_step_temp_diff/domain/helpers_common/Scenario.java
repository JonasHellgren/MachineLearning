package multi_step_temp_diff.domain.helpers_common;

import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.environments.charge.ChargeVariables;

public record Scenario<S>(String name, StateInterface<S> state, Integer simStepsMax) {

}
