package multi_step_temp_diff.domain.helpers_specific;

import lombok.Builder;
import lombok.extern.java.Log;
import multi_step_temp_diff.domain.agent_parts.replay_buffer.NstepExperience;
import multi_step_temp_diff.domain.agent_parts.replay_buffer.ReplayBufferNStepUniform;
import multi_step_temp_diff.domain.environments.charge.ChargeEnvironmentSettings;
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


    public ReplayBufferNStepUniform<ChargeVariables> createExpReplayBuffer() {
        return ReplayBufferNStepUniform.<ChargeVariables>builder()
                .buffer(createBuffer()).build();
    }



    @NotNull
    public List<NstepExperience<ChargeVariables>> createBuffer() {
        List<NstepExperience<ChargeVariables>> batch = new ArrayList<>();
        ChargeStateSuppliers suppliers=new ChargeStateSuppliers(envSettings);
        for (int i = 0; i < bufferSize; i++) {
            ChargeState state = suppliers.randomDifferentSitePositionsAndRandomSoCs();
            NstepExperience<ChargeVariables> exp = NstepExperience.<ChargeVariables>builder()
                    .stateToUpdate(state)
                    .value(stateToValueFunction.apply(state))
                    .build();
            batch.add(exp);
        }
        return batch;
    }





}
