package multi_step_temp_diff.domain.agents.maze;

import lombok.Getter;
import multi_step_temp_diff.domain.agent_abstract.*;
import multi_step_temp_diff.domain.agent_valueobj.AgentMazeNeuralSettings;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.environments.maze.MazeState;
import multi_step_temp_diff.domain.environments.maze.MazeVariables;
import multi_step_temp_diff.domain.agent_parts.NstepExperience;
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
                            AgentMazeNeuralSettings settings) {
        super(environment,
                new MazeState(MazeVariables.newFromXY(settings.startX(),settings.startY())),
                settings.discountFactor());
        this.settings= AgentMazeNeuralSettings.getDefault();
        this.memory = settings.memory();
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
