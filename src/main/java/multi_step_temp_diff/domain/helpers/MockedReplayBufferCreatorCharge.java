package multi_step_temp_diff.domain.helpers;

import common.RandUtils;
import lombok.Builder;
import lombok.extern.java.Log;
import multi_step_temp_diff.domain.agent_parts.NstepExperience;
import multi_step_temp_diff.domain.agent_parts.ReplayBufferNStep;
import multi_step_temp_diff.domain.environment_valueobj.ChargeEnvironmentSettings;
import multi_step_temp_diff.domain.environments.charge.ChargeState;
import multi_step_temp_diff.domain.environments.charge.ChargeVariables;
import multi_step_temp_diff.domain.test_helpers.ChargeStateSuppliers;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Builder
@Log
public class MockedReplayBufferCreatorCharge {

    int bufferSize;
    ChargeEnvironmentSettings settings;
    Function<ChargeState, Double> stateToValueFunction;


    public ReplayBufferNStep<ChargeVariables> createExpReplayBuffer() {
        return ReplayBufferNStep.<ChargeVariables>builder()
                .buffer(createBuffer()).build();
    }



    @NotNull
    public List<NstepExperience<ChargeVariables>> createBuffer() {
        List<NstepExperience<ChargeVariables>> batch = new ArrayList<>();
        for (int i = 0; i < bufferSize; i++) {
            ChargeState state = stateRandomPosAndSoC();
            NstepExperience<ChargeVariables> exp = NstepExperience.<ChargeVariables>builder()
                    .stateToUpdate(state)
                    .value(stateToValueFunction.apply(state))
                    .build();
            batch.add(exp);
        }
        return batch;
    }



    @NotNull
    public ChargeState stateRandomPosAndSoC() {  //todo move to ChargeStateSuppliers??
        ChargeStateSuppliers stateSuppliers=new ChargeStateSuppliers(settings);

        int posA = stateSuppliers.randomSitePos() ,posB = stateSuppliers.randomSitePos();
        while (posB==posA) {
            posB = stateSuppliers.randomSitePos();
        }

        return new ChargeState(ChargeVariables.builder()
                .posA(posA).posB(posB)
                .socA(stateSuppliers.randomSoC()).socB(stateSuppliers.randomSoC())
                .build());
    }



}
