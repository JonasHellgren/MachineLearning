package multi_step_temp_diff.domain.helpers;

import common.RandUtils;
import lombok.Builder;
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
    public ChargeState stateRandomPosAndSoC() {
        return new ChargeState(ChargeVariables.builder()
                .posA(randomPos()).posB(randomPos())
                .socA(randomSoC()).socB(randomSoC())
                .build());
    }

    public double randomSoC() {
        return RandUtils.getRandomDouble(0, 1);
    }

    public int randomPos() {
        RandUtils<Integer> randUtils=new RandUtils<>();
        List<Integer> es = new ArrayList<>(settings.siteNodes());
        return randUtils.getRandomItemFromList(es);
    }

}
