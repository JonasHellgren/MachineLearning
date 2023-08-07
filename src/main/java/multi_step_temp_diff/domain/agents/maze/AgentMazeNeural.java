package multi_step_temp_diff.domain.agents.maze;

import lombok.Getter;
import multi_step_temp_diff.domain.agent_abstract.*;
import multi_step_temp_diff.domain.agent_valueobj.AgentMazeNeuralSettings;
import multi_step_temp_diff.domain.agent_valueobj.NetSettings;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.environments.maze.MazeState;
import multi_step_temp_diff.domain.environments.maze.MazeVariables;
import multi_step_temp_diff.domain.agent_parts.NstepExperience;
import org.neuroph.util.TransferFunctionType;

import java.util.List;

@Getter
public class AgentMazeNeural extends AgentAbstract<MazeVariables> implements AgentNeuralInterface<MazeVariables> {

    AgentMazeNeuralSettings settings;
    NetworkMemoryInterface<MazeVariables>  memory;


    public static AgentMazeNeural newDefault(EnvironmentInterface<MazeVariables> environment) {
        return new AgentMazeNeural(environment, AgentMazeNeuralSettings.getDefault());
    }

    public static AgentMazeNeural newFromSettings(EnvironmentInterface<MazeVariables> environment,
                                                  AgentMazeNeuralSettings settings) {
        return new AgentMazeNeural(environment, settings);
    }

    public static AgentMazeNeural newWithDiscountFactorAndLearningRate(EnvironmentInterface<MazeVariables> environment,
                                                        double discountFactor,double learningRate) {

        var settingsAdapted=AgentMazeNeuralSettings.getWithDiscountAndLearningRate(discountFactor,learningRate);
        return new AgentMazeNeural(environment, settingsAdapted);
    }

    private AgentMazeNeural(EnvironmentInterface<MazeVariables> environment,
                            AgentMazeNeuralSettings agentSettings) {
        super(environment,
                new MazeState(MazeVariables.newFromXY(agentSettings.startX(),agentSettings.startY())),
                agentSettings.discountFactor());
        this.settings= AgentMazeNeuralSettings.getDefault();

        Integer inputSize = agentSettings.nofStates();
        NetSettings netSettings = NetSettings.builder()
                .inputSize(inputSize).nofNeuronsHidden(agentSettings.nofStates())
                .nofHiddenLayers(1).transferFunctionType(TransferFunctionType.TANH)
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


}
