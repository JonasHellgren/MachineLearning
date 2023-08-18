package multi_step_temp_diff.domain.agents.charge;

import multi_step_temp_diff.domain.agent_abstract.NetworkMemoryInterface;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.agent_abstract.ValueMemoryNetworkAbstract;
import multi_step_temp_diff.domain.agent_valueobj.NetSettings;
import multi_step_temp_diff.domain.agents.charge.input_vector_setter.InputVectorSetterChargeInterface;
import multi_step_temp_diff.domain.environments.charge.ChargeState;

public class NeuralValueMemoryCharge<S> extends ValueMemoryNetworkAbstract<S>  {

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

    @Override
    public NetworkMemoryInterface<S> copy() {
        ValueMemoryNetworkAbstract<S> netCopy=new NeuralValueMemoryCharge<>(this.netSettings,inputVectorSetterCharge);
        netCopy.copyWeights(this);
        return netCopy;
    }




}
