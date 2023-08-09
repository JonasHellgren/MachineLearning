package multi_step_temp_diff.domain.helpers_specific;

import lombok.Builder;
import lombok.extern.java.Log;
import multi_step_temp_diff.domain.agent_parts.NstepExperience;
import multi_step_temp_diff.domain.agent_parts.ReplayBufferNStep;
import multi_step_temp_diff.domain.environment_valueobj.ChargeEnvironmentSettings;
import multi_step_temp_diff.domain.environments.charge.ChargeState;
import multi_step_temp_diff.domain.environments.charge.ChargeVariables;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Builder
@Log
public class ChargeMockedReplayBufferCreator {

    int bufferSize;
    ChargeEnvironmentSettings envSettings;
    Function<ChargeState, Double> stateToValueFunction;


    public ReplayBufferNStep<ChargeVariables> createExpReplayBuffer() {
        return ReplayBufferNStep.<ChargeVariables>builder()
                .buffer(createBuffer()).build();
    }



    @NotNull
    public List<NstepExperience<ChargeVariables>> createBuffer() {
        List<NstepExperience<ChargeVariables>> batch = new ArrayList<>();
        ChargeStateSuppliers suppliers=new ChargeStateSuppliers(envSettings);
        for (int i = 0; i < bufferSize; i++) {
            ChargeState state = suppliers.stateRandomPosAndSoC();
            NstepExperience<ChargeVariables> exp = NstepExperience.<ChargeVariables>builder()
                    .stateToUpdate(state)
                    .value(stateToValueFunction.apply(state))
                    .build();
            batch.add(exp);
        }
        return batch;
    }





}
