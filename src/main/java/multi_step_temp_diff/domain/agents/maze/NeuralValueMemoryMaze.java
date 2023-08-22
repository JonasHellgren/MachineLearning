package multi_step_temp_diff.domain.agents.maze;

import multi_step_temp_diff.domain.agent_parts.neural_memory.NetworkMemoryInterface;
import multi_step_temp_diff.domain.environments.maze.MazeState;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.agent_parts.neural_memory.ValueMemoryNeuralAbstract;
import multi_step_temp_diff.domain.agent_valueobj.NetSettings;

import java.util.Arrays;

import static multi_step_temp_diff.domain.environments.maze.MazeEnvironment.settings;

/***
 * For every dimension there is one hot encoded vector. Example x=1, y=1: in=[inx iny]=[0,1,0,0,0, 0,1,0,0,0,0]
 *
 */

public class NeuralValueMemoryMaze<S> extends ValueMemoryNeuralAbstract<S> {

    private static final double MARGIN = 1.0;

    public NeuralValueMemoryMaze(NetSettings settings) {
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

    @Override
    public NetworkMemoryInterface<S> copy() {
        NeuralValueMemoryMaze<S> netCopy=new NeuralValueMemoryMaze<>(this.netSettings);
        netCopy.copyWeights(this);
        return netCopy;
    }

}
