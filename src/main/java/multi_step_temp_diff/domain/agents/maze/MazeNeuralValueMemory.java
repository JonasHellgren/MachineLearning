package multi_step_temp_diff.domain.agents.maze;

import multi_step_temp_diff.domain.environment_valueobj.MazeEnvironmentSettings;
import multi_step_temp_diff.domain.environments.maze.MazeEnvironment;
import multi_step_temp_diff.domain.environments.maze.MazeState;
import multi_step_temp_diff.domain.agent_abstract.PersistentMemoryInterface;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.agent_abstract.ValueMemoryNetworkAbstract;
import multi_step_temp_diff.domain.agent_valueobj.NetSettings;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.util.TransferFunctionType;

import java.util.Arrays;

/***
 * For every dimension there is one hot encoded vector. Example x=1, y=1: in=[inx iny]=[0,1,0,0,0, 0,1,0,0,0,0]
 *
 */

public class MazeNeuralValueMemory <S> extends ValueMemoryNetworkAbstract<S> implements PersistentMemoryInterface {

    private static final double MARGIN = 1.0;
    static final MazeEnvironmentSettings settings=MazeEnvironmentSettings.getDefault();

    public MazeNeuralValueMemory(double learningRate) {
        this(NetSettings.builder()
                .inputSize(settings.nofCols()+ settings.nofRows())
                .nofNeuronsHidden((int) ((settings.nofCols()+ settings.nofRows())*1.0))
                .outPutSize(1)
                .learningRate(learningRate)
                .minOut(settings.rewardGoal()*0.00).maxOut(settings.rewardGoal()*2)
                .build());
    }

    public MazeNeuralValueMemory(NetSettings settings) {
        neuralNetwork = new MultiLayerPerceptron(
                TransferFunctionType.TANH,
                settings.inputSize(),
                settings.nofNeuronsHidden(), //  settings.nofNeuronsHidden,   settings.nofNeuronsHidden,
                settings.outPutSize());
        super.netSettings = settings;
        super.createLearningRule(neuralNetwork, settings);
        super.createOutScalers(settings.minOut() * MARGIN, settings.maxOut() * MARGIN);
        isWarmedUp = false;
    }

    @Override
    public double[] getInputVec(StateInterface<S> s) {
        double[] inArray = new double[super.netSettings.inputSize()];
        Arrays.fill(inArray, 0);
        MazeState stateCasted=(MazeState) s;
        inArray[MazeState.getX.apply(stateCasted)] = 1d;
        inArray[settings.nofCols()+MazeState.getY.apply(stateCasted)] = 1d;
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
