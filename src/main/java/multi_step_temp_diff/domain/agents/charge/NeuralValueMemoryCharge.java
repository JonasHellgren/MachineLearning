package multi_step_temp_diff.domain.agents.charge;

import multi_step_temp_diff.domain.agent_abstract.PersistentMemoryInterface;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.agent_abstract.ValueMemoryNetworkAbstract;
import multi_step_temp_diff.domain.agent_valueobj.NetSettings;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.util.TransferFunctionType;

public class NeuralValueMemoryCharge<S> extends ValueMemoryNetworkAbstract<S> implements PersistentMemoryInterface {
    private static final double MARGIN = 1.0;

    public NeuralValueMemoryCharge(NetSettings settings) {
        neuralNetwork = new MultiLayerPerceptron(
                TransferFunctionType.TANH,
                settings.inputSize(),
                settings.nofNeuronsHidden(), settings.nofNeuronsHidden(),
                settings.outPutSize());
        super.netSettings = settings;
        super.createLearningRule(neuralNetwork, settings);
        super.createOutScalers(settings.minOut() * MARGIN, settings.maxOut() * MARGIN);
        isWarmedUp = false;
    }

    @Override
    public double[] getInputVec(StateInterface<S> state) {
        return new double[0];
    }
}
