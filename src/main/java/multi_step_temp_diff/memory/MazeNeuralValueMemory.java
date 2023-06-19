package multi_step_temp_diff.memory;

import multi_step_temp_diff.agents.AgentMazeNeural;
import multi_step_temp_diff.environments.ForkEnvironment;
import multi_step_temp_diff.environments.ForkState;
import multi_step_temp_diff.environments.MazeEnvironment;
import multi_step_temp_diff.environments.MazeState;
import multi_step_temp_diff.interfaces_and_abstract.PersistentMemoryInterface;
import multi_step_temp_diff.interfaces_and_abstract.StateInterface;
import multi_step_temp_diff.interfaces_and_abstract.ValueMemoryNetworkAbstract;
import multi_step_temp_diff.models.NetSettings;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.util.TransferFunctionType;

import java.util.Arrays;

/***
 * For every dimension there is one hot encoded vector. Example x=1, y=1: in=[inx iny]=[0,1,0,0,0, 0,1,0,0,0,0]
 *
 */

public class MazeNeuralValueMemory <S> extends ValueMemoryNetworkAbstract<S> implements PersistentMemoryInterface {

    private static final double MARGIN = 1.0;

    public MazeNeuralValueMemory() {
        this(NetSettings.builder()
                .inputSize(MazeEnvironment.NOF_COLS+MazeEnvironment.NOF_ROWS)
                .nofNeuronsHidden(MazeEnvironment.NOF_COLS+MazeEnvironment.NOF_ROWS)
                .outPutSize(1)
                .learningRate(AgentMazeNeural.LEARNING_RATE)
                .minOut(-MazeEnvironment.REWARD_GOAL)
                .maxOut(MazeEnvironment.REWARD_GOAL)
                .build());
    }

    public MazeNeuralValueMemory(NetSettings settings) {
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
        MazeState stateCasted=(MazeState) s;
        inArray[MazeState.getX.apply(stateCasted)] = 1d;
        inArray[MazeEnvironment.NOF_COLS+MazeState.getY.apply(stateCasted)] = 1d;
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
