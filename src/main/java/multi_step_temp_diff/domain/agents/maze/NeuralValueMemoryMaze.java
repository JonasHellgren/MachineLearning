package multi_step_temp_diff.domain.agents.maze;

import multi_step_temp_diff.domain.environments.maze.MazeState;
import multi_step_temp_diff.domain.agent_abstract.PersistentMemoryInterface;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.agent_abstract.ValueMemoryNetworkAbstract;
import multi_step_temp_diff.domain.agent_valueobj.NetSettings;
import multi_step_temp_diff.domain.normalizer.NormalizeMinMax;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.util.TransferFunctionType;

import java.util.Arrays;

import static multi_step_temp_diff.domain.environments.maze.MazeEnvironment.settings;

/***
 * For every dimension there is one hot encoded vector. Example x=1, y=1: in=[inx iny]=[0,1,0,0,0, 0,1,0,0,0,0]
 *
 */

public class NeuralValueMemoryMaze<S> extends ValueMemoryNetworkAbstract<S> implements PersistentMemoryInterface {

    private static final double MARGIN = 1.0;

    public NeuralValueMemoryMaze(NetSettings settings) {
      /*  super(new MultiLayerPerceptron(
                        TransferFunctionType.TANH,
                        settings.inputSize(),
                        settings.nofNeuronsHidden(), // settings.nofNeuronsHidden(),
                        settings.outPutSize()),
    */


    super(settings);

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
