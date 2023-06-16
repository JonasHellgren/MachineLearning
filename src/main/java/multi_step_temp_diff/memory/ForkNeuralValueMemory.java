package multi_step_temp_diff.memory;

import multi_step_temp_diff.environments.ForkEnvironment;
import multi_step_temp_diff.environments.ForkState;
import multi_step_temp_diff.interfaces_and_abstract.PersistentMemoryInterface;
import multi_step_temp_diff.interfaces_and_abstract.StateInterface;
import multi_step_temp_diff.models.NetSettings;
import multi_step_temp_diff.interfaces_and_abstract.ValueMemoryNetworkAbstract;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.util.TransferFunctionType;
import java.util.Arrays;

/**
 * Input is a binary vector with zeros except at active state. Much more stable than one double input.
 */

public class ForkNeuralValueMemory<S> extends ValueMemoryNetworkAbstract<S> implements PersistentMemoryInterface {

    private static final double MARGIN = 1.0;

    public ForkNeuralValueMemory() {
        this(NetSettings.builder()
                .inputSize(ForkEnvironment.NOF_STATES).nofNeuronsHidden(ForkEnvironment.NOF_STATES)
                .minOut(ForkEnvironment.R_HELL).maxOut(ForkEnvironment.R_HEAVEN).build());
    }

    public ForkNeuralValueMemory(NetSettings settings) {
        neuralNetwork = new MultiLayerPerceptron(
                TransferFunctionType.TANH,
                settings.inputSize,
                settings.nofNeuronsHidden, settings.nofNeuronsHidden,
                settings.outPutSize);
        super.settings = settings;
        super.createLearningRule(neuralNetwork, settings);
        super.createOutScalers(settings.minOut * MARGIN, settings.maxOut * MARGIN);
        isWarmedUp = false;
    }

    @Override
    public double[] getInputVec(StateInterface<S> s) {
        double[] inArray = new double[settings.inputSize];
        Arrays.fill(inArray, 0);
        ForkState stateCasted=(ForkState) s;
        inArray[stateCasted.getVariables().position] = 1d;
        return inArray;
    }

    @Override
    public void save(String fileName) {
        super.save(fileName);
    }

    @Override
    public void load(String fileName) {
        super.load(fileName);
    }

}
