package multi_step_temp_diff.domain.agents.charge.input_vector_setter;

import common.Conditionals;
import lombok.NonNull;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.agent_valueobj.AgentChargeNeuralSettings;
import multi_step_temp_diff.domain.environment_valueobj.ChargeEnvironmentSettings;
import multi_step_temp_diff.domain.environments.charge.ChargeState;
import multi_step_temp_diff.domain.environments.charge.ChargeVariables;
import multi_step_temp_diff.domain.environments.fork.ForkState;

import java.util.Arrays;
import java.util.Optional;

import static common.Conditionals.executeIfTrue;
import static java.lang.System.out;
import static multi_step_temp_diff.domain.environments.charge.ChargeState.*;

public class InputSetterSoCAtOccupiedZeroOther<S> implements InputVectorSetterChargeInterface<S> {

    AgentChargeNeuralSettings agentSettings;
    PositionMapper positionMapper;

    public InputSetterSoCAtOccupiedZeroOther(@NonNull AgentChargeNeuralSettings agentSettings,
                                             @NonNull ChargeEnvironmentSettings environmentSettings) {
        this.agentSettings = agentSettings;
        this.positionMapper=new PositionMapper(agentSettings,environmentSettings);
    }

    @Override
    public double[] defineInArray(StateInterface<S> state) {
        double[] inArray = new double[agentSettings.nofStates()];
        Arrays.fill(inArray, 0);

        ChargeState stateCasted=(ChargeState) state;
        Optional<Integer> mappedPosA=positionMapper.map(posA.apply(stateCasted));
        Optional<Integer> mappedPosB=positionMapper.map(posB.apply(stateCasted));

        executeIfTrue(mappedPosA.isPresent(), () ->
            inArray[mappedPosA.orElseThrow()] = socA.apply(stateCasted));

        executeIfTrue(mappedPosB.isPresent(), () ->
            inArray[mappedPosB.orElseThrow()] = socB.apply(stateCasted));

        return inArray;
    }


}
