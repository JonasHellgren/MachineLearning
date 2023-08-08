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
import static multi_step_temp_diff.domain.environments.charge.ChargeState.socB;

public class HotEncodingOneAtOccupiedSoCsSeparate implements InputVectorSetterChargeInterface {

    public static final double VALUE_NOT_OCCUPIED = 0, VALUE_OCCUPIED = 1d;
    AgentChargeNeuralSettings agentSettings;
    ChargeEnvironmentSettings environmentSettings;
    PositionMapper positionMapper;
    NormalizerInterface normalizer;

    public HotEncodingOneAtOccupiedSoCsSeparate(@NonNull AgentChargeNeuralSettings agentSettings,
                                                @NonNull ChargeEnvironmentSettings environmentSettings,
                                                @NonNull NormalizerInterface normalizer) {
        this.agentSettings = agentSettings;
        this.environmentSettings=environmentSettings;
        this.positionMapper=new PositionMapper(agentSettings,environmentSettings);
        this.normalizer=normalizer;
    }

    @Override
    public double[] defineInArray(ChargeState state) {
        int inArrLength = inputSize() ;
        double[] inArray = new double[inputSize()];
        Arrays.fill(inArray, normalizer.normalize(VALUE_NOT_OCCUPIED));

        Optional<Integer> mappedPosA=positionMapper.map(posA.apply(state));
        Optional<Integer> mappedPosB=positionMapper.map(posB.apply(state));

        executeIfTrue(mappedPosA.isPresent(), () -> {
            inArray[mappedPosA.orElseThrow()] = VALUE_OCCUPIED;
            inArray[inArrLength - 2] = socA.apply(state);
        });

        executeIfTrue(mappedPosB.isPresent(), () -> {
            inArray[mappedPosB.orElseThrow()] = VALUE_OCCUPIED;
            inArray[inArrLength - 1] = socB.apply(state);
        });

        return inArray;
    }

    @Override
    public int inputSize() {
        return agentSettings.nofStates()+environmentSettings.nofVehicles();
    }

}
