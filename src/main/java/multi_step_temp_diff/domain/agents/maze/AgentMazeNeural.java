package multi_step_temp_diff.domain.agents.maze;

import lombok.Getter;
import lombok.SneakyThrows;
import multi_step_temp_diff.domain.agent_abstract.*;
import multi_step_temp_diff.domain.agent_parts.neural_memory.NetworkMemoryInterface;
import multi_step_temp_diff.domain.agent_valueobj.AgentMazeNeuralSettings;
import multi_step_temp_diff.domain.agent_valueobj.NetSettings;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.environments.maze.MazeState;
import multi_step_temp_diff.domain.environments.maze.MazeVariables;
import multi_step_temp_diff.domain.agent_parts.replay_buffer.NstepExperience;
import org.neuroph.util.TransferFunctionType;

import java.util.List;

@Getter
public class AgentMazeNeural extends AgentAbstract<MazeVariables> implements AgentNeuralInterface<MazeVariables> {

    AgentMazeNeuralSettings settings;
    NetworkMemoryInterface<MazeVariables> memory;


    public static AgentMazeNeural newDefault(EnvironmentInterface<MazeVariables> environment) {
        return new AgentMazeNeural(environment, AgentMazeNeuralSettings.newDefault());
    }

    public AgentMazeNeural(EnvironmentInterface<MazeVariables> environment,
                            AgentMazeNeuralSettings agentSettings) {
        super(
                environment,new MazeState(MazeVariables.newFromXY(agentSettings.startX(),agentSettings.startY())),agentSettings);
        this.settings= AgentMazeNeuralSettings.newDefault();

        Integer inputSize = agentSettings.nofStates();
        NetSettings netSettings = NetSettings.builder()
                .inputSize(inputSize).nofNeuronsHidden(inputSize)
                .nofHiddenLayers(agentSettings.nofLayersHidden()).transferFunctionType(TransferFunctionType.TANH)
                .minOut(agentSettings.minValue()).maxOut(agentSettings.maxValue())
                .learningRate(agentSettings.learningRate())
                .normalizer(agentSettings.normalizer())
                .build();

        System.out.println("netSettings = " + netSettings);

        this.memory = new NeuralValueMemoryMaze<>(netSettings);
    }

    @Override
    public double readValue(StateInterface<MazeVariables> state) {
        return memory.read(state);
    }

    @Override
    public void learn(List<NstepExperience<MazeVariables>> miniBatch) {
        memory.learn(miniBatch);
    }

    @SneakyThrows
    @Override
    public void saveMemory(String fileName) {
        throw new NoSuchMethodException("Maze");
    }

    @SneakyThrows
    @Override
    public void loadMemory(String fileName) {
        throw new NoSuchMethodException("Maze");
    }


}
