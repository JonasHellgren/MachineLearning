package multi_step_temp_diff.domain.agents.charge.input_vector_setter;

import lombok.NonNull;
import multi_step_temp_diff.domain.agents.charge.AgentChargeNeuralSettings;
import multi_step_temp_diff.domain.environments.charge.ChargeEnvironmentSettings;
import multi_step_temp_diff.domain.environments.charge.ChargeState;
import multi_step_temp_diff.domain.agent_parts.neural_memory.normalizer.NormalizerInterface;

import java.util.Arrays;
import java.util.Optional;
import static common.Conditionals.executeIfTrue;
import static multi_step_temp_diff.domain.environments.charge.ChargeState.*;

public class HotEncodingSoCAtOccupiedElseValue implements InputVectorSetterChargeInterface {

    AgentChargeNeuralSettings agentSettings;
    PositionMapper positionMapper;
    NormalizerInterface normalizer;
    Double valueIfNotOccupied;

    public HotEncodingSoCAtOccupiedElseValue(@NonNull AgentChargeNeuralSettings agentSettings,
                                             @NonNull ChargeEnvironmentSettings environmentSettings,
                                             @NonNull NormalizerInterface normalizer,
                                             @NonNull  Double value) {
        this.agentSettings = agentSettings;
        this.positionMapper=new PositionMapper(agentSettings,environmentSettings);
        this.normalizer=normalizer;
        this.valueIfNotOccupied=value;
    }


    @Override
    public double[] defineInArray(ChargeState state) {
        double[] inArray = new double[inputSize()];
        double normalizedValue = normalizer.normalize(valueIfNotOccupied);
        Arrays.fill(inArray, normalizedValue);

        Optional<Integer> mappedPosA=positionMapper.map(posA.apply(state));
        Optional<Integer> mappedPosB=positionMapper.map(posB.apply(state));

        executeIfTrue(mappedPosA.isPresent(), () ->
            inArray[mappedPosA.orElseThrow()] = normalizer.normalize(socA.apply(state)));

        executeIfTrue(mappedPosB.isPresent(), () ->
            inArray[mappedPosB.orElseThrow()] = normalizer.normalize(socB.apply(state)));

        return inArray;
    }

    @Override
    public int inputSize() {
        return agentSettings.nofStates();
    }


}
