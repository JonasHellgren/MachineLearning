package multi_step_temp_diff.domain.agents.charge;

import common.CpuTimer;
import multi_step_temp_diff.domain.agent_abstract.PersistentMemoryInterface;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.agent_abstract.ValueMemoryNetworkAbstract;
import multi_step_temp_diff.domain.agent_parts.ReplayBufferNStep;
import multi_step_temp_diff.domain.agent_valueobj.NetSettings;
import multi_step_temp_diff.domain.agents.charge.input_vector_setter.InputVectorSetterChargeInterface;
import multi_step_temp_diff.domain.environment_valueobj.ChargeEnvironmentSettings;
import multi_step_temp_diff.domain.environments.charge.ChargeEnvironmentLambdas;
import multi_step_temp_diff.domain.environments.charge.ChargeState;
import multi_step_temp_diff.domain.helpers.MockedReplayBufferCreatorCharge;
import multi_step_temp_diff.domain.test_helpers.StateToValueFunctionContainerCharge;

public class NeuralValueMemoryCharge<S> extends ValueMemoryNetworkAbstract<S> implements PersistentMemoryInterface {

    InputVectorSetterChargeInterface inputVectorSetterCharge;

    public NeuralValueMemoryCharge(NetSettings settings, InputVectorSetterChargeInterface inputVectorSetterCharge) {
        super(settings);
        this.inputVectorSetterCharge = inputVectorSetterCharge;
    }

    @Override
    public double[] getInputVec(StateInterface<S> state) {
        ChargeState stateCasted = (ChargeState) state;
        return inputVectorSetterCharge.defineInArray(stateCasted);
    }



}
