package multi_step_temp_diff.agents;

import lombok.NonNull;
import multi_step_temp_diff.domain.interfaces_and_abstract.AgentAbstract;
import multi_step_temp_diff.domain.interfaces_and_abstract.AgentInterface;
import multi_step_temp_diff.domain.interfaces_and_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.interfaces_and_abstract.StateInterface;
import multi_step_temp_diff.environments.ChargeVariables;

public class AgentChargeGreedy extends AgentAbstract<ChargeVariables> implements AgentInterface<ChargeVariables> {

    public static final int DISCOUNT_FACTOR = 1;

    public AgentChargeGreedy(@NonNull EnvironmentInterface<ChargeVariables> environment,
                             @NonNull StateInterface<ChargeVariables> state) {
        super(environment, state, DISCOUNT_FACTOR);
    }

    @Override
    public double readValue(StateInterface<ChargeVariables> state) {
        return 0;
    }

    @Override
    public int chooseAction(double probRandom) {
        return super.chooseAction(0);
    }


}
