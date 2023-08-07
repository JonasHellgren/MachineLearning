package multi_step_temp_diff.domain.agents.charge.input_vector_setter;

import lombok.NonNull;
import multi_step_temp_diff.domain.agent_valueobj.AgentChargeNeuralSettings;
import multi_step_temp_diff.domain.environment_valueobj.ChargeEnvironmentSettings;
import multi_step_temp_diff.domain.environments.charge.ChargeState;
import java.util.Arrays;
import java.util.Optional;
import static common.Conditionals.executeIfTrue;
import static multi_step_temp_diff.domain.environments.charge.ChargeState.*;

public class InputSetterSoCAtOccupiedZeroOther implements InputVectorSetterChargeInterface {

    AgentChargeNeuralSettings agentSettings;
    PositionMapper positionMapper;

    public InputSetterSoCAtOccupiedZeroOther(@NonNull AgentChargeNeuralSettings agentSettings,
                                             @NonNull ChargeEnvironmentSettings environmentSettings) {
        this.agentSettings = agentSettings;
        this.positionMapper=new PositionMapper(agentSettings,environmentSettings);
    }

    @Override
    public double[] defineInArray(ChargeState state) {
        double[] inArray = new double[agentSettings.nofStates()];
        Arrays.fill(inArray, 0);

        Optional<Integer> mappedPosA=positionMapper.map(posA.apply(state));
        Optional<Integer> mappedPosB=positionMapper.map(posB.apply(state));

        executeIfTrue(mappedPosA.isPresent(), () ->
            inArray[mappedPosA.orElseThrow()] = socA.apply(state));

        executeIfTrue(mappedPosB.isPresent(), () ->
            inArray[mappedPosB.orElseThrow()] = socB.apply(state));

        return inArray;
    }


}
