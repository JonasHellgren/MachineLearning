package multi_step_temp_diff.domain.agents.charge;

import multi_step_temp_diff.domain.agent_abstract.PersistentMemoryInterface;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.agent_abstract.ValueMemoryNetworkAbstract;
import multi_step_temp_diff.domain.agent_valueobj.NetSettings;
import multi_step_temp_diff.domain.agents.charge.input_vector_setter.InputVectorSetterChargeInterface;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.util.TransferFunctionType;

public class NeuralValueMemoryCharge<S> extends ValueMemoryNetworkAbstract<S> implements PersistentMemoryInterface {
    private static final double MARGIN = 1.0;

    InputVectorSetterChargeInterface<S> inputVectorSetterCharge;

    public NeuralValueMemoryCharge(NetSettings settings, InputVectorSetterChargeInterface<S> inputVectorSetterCharge) {
        neuralNetwork = new MultiLayerPerceptron(
                TransferFunctionType.TANH,
                settings.inputSize(),
                settings.nofNeuronsHidden(), settings.nofNeuronsHidden(),
                settings.outPutSize());
        super.netSettings = settings;
        super.createLearningRule(neuralNetwork, settings);
        super.createOutScalers(settings.minOut() * MARGIN, settings.maxOut() * MARGIN);
        super.isWarmedUp = false;
        this.inputVectorSetterCharge=inputVectorSetterCharge;
    }

    @Override
    public double[] getInputVec(StateInterface<S> state) {

        return inputVectorSetterCharge.defineInArray(state);
    }
}
