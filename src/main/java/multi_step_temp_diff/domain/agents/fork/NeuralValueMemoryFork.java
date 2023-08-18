package multi_step_temp_diff.domain.agents.fork;

import lombok.SneakyThrows;
import multi_step_temp_diff.domain.agent_abstract.NetworkMemoryInterface;
import multi_step_temp_diff.domain.agents.charge.NeuralValueMemoryCharge;
import multi_step_temp_diff.domain.environments.fork.ForkState;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.agent_valueobj.NetSettings;
import multi_step_temp_diff.domain.agent_abstract.ValueMemoryNetworkAbstract;
import multi_step_temp_diff.domain.agent_abstract.normalizer.NormalizeMinMax;
import org.neuroph.util.TransferFunctionType;
import java.util.Arrays;

import static multi_step_temp_diff.domain.environments.fork.ForkEnvironment.envSettings;

/**
 * Input is a binary vector with zeros except at active state. Much more stable than one double input.
 */

public class NeuralValueMemoryFork<S> extends ValueMemoryNetworkAbstract<S>  {

    private static final double MARGIN = 1.0;

    public NeuralValueMemoryFork() {  //todo, remove confusing with envSettings
        this(NetSettings.builder()
                .inputSize(envSettings.nofStates()).nofNeuronsHidden(envSettings.nofStates())
                .minOut(envSettings.rewardHell()).maxOut(envSettings.rewardHeaven())
                .nofHiddenLayers(1).transferFunctionType(TransferFunctionType.TANH)
                .normalizer(new NormalizeMinMax(envSettings.rewardHell(),envSettings.rewardHeaven())).build());
    }

    public NeuralValueMemoryFork(NetSettings settings) {
        /* super(new MultiLayerPerceptron(
                        TransferFunctionType.TANH,
                        settings.inputSize(),
                        settings.nofNeuronsHidden(), settings.nofNeuronsHidden(),
                        settings.outPutSize()),
                */
           super(settings);
        isWarmedUp = false;
    }

    @Override
    public double[] getInputVec(StateInterface<S> s) {
        double[] inArray = new double[netSettings.inputSize()];
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

    @Override
    public NetworkMemoryInterface<S> copy() {
        ValueMemoryNetworkAbstract<S> netCopy=new NeuralValueMemoryFork<>(this.netSettings);
        netCopy.copyWeights(this);
        return netCopy;
    }


}
