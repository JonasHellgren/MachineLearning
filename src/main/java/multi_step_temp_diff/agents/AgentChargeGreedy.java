package multi_step_temp_diff.agents;

import lombok.NonNull;
import multi_step_temp_diff.environments.ChargeVariables;
import multi_step_temp_diff.environments.MazeVariables;
import multi_step_temp_diff.interfaces_and_abstract.*;

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
