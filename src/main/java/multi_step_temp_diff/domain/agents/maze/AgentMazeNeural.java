package multi_step_temp_diff.domain.agents.maze;

import lombok.Builder;
import lombok.Getter;
import multi_step_temp_diff.domain.agent_abstract.*;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.environments.maze.MazeState;
import multi_step_temp_diff.domain.environments.maze.MazeVariables;
import multi_step_temp_diff.domain.agent_parts.NstepExperience;
import java.util.List;

@Getter
public class AgentMazeNeural extends AgentAbstract<MazeVariables> implements AgentNeuralInterface<MazeVariables> {

    private static final int START_X = 0, START_Y=0;
    public  static final double DISCOUNT_FACTOR=1, LEARNING_RATE=0.5;
    static final NetworkMemoryInterface<MazeVariables> MEMORY=new MazeNeuralValueMemory<>(LEARNING_RATE);

    NetworkMemoryInterface<MazeVariables>  memory;

    @Builder
    private AgentMazeNeural(EnvironmentInterface<MazeVariables> environment,
                            StateInterface<MazeVariables> state,
                            double discountFactor,
                            NetworkMemoryInterface<MazeVariables> memory) {
        super(environment,state,discountFactor);
        this.memory = memory;
    }

    public static AgentMazeNeural newDefault(EnvironmentInterface<MazeVariables> environment) {
        return AgentMazeNeural.newWithDiscountFactor(environment,DISCOUNT_FACTOR);
    }

    public static AgentMazeNeural newWithDiscountFactor(EnvironmentInterface<MazeVariables> environment,
                                                        double discountFactor) {
        return AgentMazeNeural.builder()
                .environment(environment)
                .state(MazeState.newFromXY(START_X,START_Y)).discountFactor(discountFactor)
                .memory(MEMORY).build();
    }

    public static AgentMazeNeural newWithDiscountFactorAndLearningRate(EnvironmentInterface<MazeVariables> environment,
                                                        double discountFactor,double learningRate) {
        return AgentMazeNeural.builder()
                .environment(environment)
                .state(MazeState.newFromXY(START_X,START_Y)).discountFactor(discountFactor)
                .memory(new MazeNeuralValueMemory<>(learningRate)).build();
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
