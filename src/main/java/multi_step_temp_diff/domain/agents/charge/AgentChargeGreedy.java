package multi_step_temp_diff.domain.agents.charge;

import lombok.NonNull;
import multi_step_temp_diff.domain.agent_abstract.AgentAbstract;
import multi_step_temp_diff.domain.agent_abstract.AgentInterface;
import multi_step_temp_diff.domain.agent_valueobj.AgentChargeNeuralSettings;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.environments.charge.ChargeVariables;

public class AgentChargeGreedy extends AgentAbstract<ChargeVariables> implements AgentInterface<ChargeVariables> {

    public static final int DISCOUNT_FACTOR = 1;

    public AgentChargeGreedy(@NonNull EnvironmentInterface<ChargeVariables> environment,
                             @NonNull StateInterface<ChargeVariables> state,
                             @NonNull AgentChargeNeuralSettings agentSettings) {
        super(environment, state, agentSettings);
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
