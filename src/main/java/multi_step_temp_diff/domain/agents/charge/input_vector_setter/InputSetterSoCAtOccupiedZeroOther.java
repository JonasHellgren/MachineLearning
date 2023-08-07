package multi_step_temp_diff.domain.agents.charge.input_vector_setter;

import lombok.NonNull;
import multi_step_temp_diff.domain.agent_valueobj.AgentChargeNeuralSettings;
import multi_step_temp_diff.domain.environment_valueobj.ChargeEnvironmentSettings;
import multi_step_temp_diff.domain.environments.charge.ChargeState;
import multi_step_temp_diff.domain.normalizer.NormalizerInterface;

import java.util.Arrays;
import java.util.Optional;
import static common.Conditionals.executeIfTrue;
import static multi_step_temp_diff.domain.environments.charge.ChargeState.*;

public class InputSetterSoCAtOccupiedZeroOther implements InputVectorSetterChargeInterface {

    AgentChargeNeuralSettings agentSettings;
    PositionMapper positionMapper;
    NormalizerInterface normalizer;

    public InputSetterSoCAtOccupiedZeroOther(@NonNull AgentChargeNeuralSettings agentSettings,
                                             @NonNull ChargeEnvironmentSettings environmentSettings,
                                             @NonNull NormalizerInterface normalizer) {
        this.agentSettings = agentSettings;
        this.positionMapper=new PositionMapper(agentSettings,environmentSettings);
        this.normalizer=normalizer;
    }

    @Override
    public double[] defineInArray(ChargeState state) {
        double[] inArray = new double[agentSettings.nofStates()];
        Arrays.fill(inArray, 0);

        Optional<Integer> mappedPosA=positionMapper.map(posA.apply(state));
        Optional<Integer> mappedPosB=positionMapper.map(posB.apply(state));

        executeIfTrue(mappedPosA.isPresent(), () ->
            inArray[mappedPosA.orElseThrow()] = normalizer.normalize(socA.apply(state)));

        executeIfTrue(mappedPosB.isPresent(), () ->
            inArray[mappedPosB.orElseThrow()] = normalizer.normalize(socB.apply(state)));

        return inArray;
    }


}
