package multi_step_temp_diff.domain.agents.charge;

import multi_step_temp_diff.domain.agent_abstract.PersistentMemoryInterface;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.agent_abstract.ValueMemoryNetworkAbstract;
import multi_step_temp_diff.domain.agent_valueobj.NetSettings;
import multi_step_temp_diff.domain.agents.charge.input_vector_setter.InputVectorSetterChargeInterface;
import multi_step_temp_diff.domain.environments.charge.ChargeState;
import multi_step_temp_diff.domain.normalizer.NormalizeMinMax;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.util.TransferFunctionType;

public class NeuralValueMemoryCharge<S> extends ValueMemoryNetworkAbstract<S> implements PersistentMemoryInterface {
    private static final double MARGIN = 1.0;

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
