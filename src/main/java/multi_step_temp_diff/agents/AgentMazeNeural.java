package multi_step_temp_diff.agents;

import lombok.Builder;
import lombok.Getter;
import lombok.SneakyThrows;
import multi_step_temp_diff.environments.MazeState;
import multi_step_temp_diff.environments.MazeVariables;
import multi_step_temp_diff.interfaces_and_abstract.*;
import multi_step_temp_diff.memory.MazeNeuralValueMemory;
import multi_step_temp_diff.models.NstepExperience;
import java.util.List;

@Getter
public class AgentMazeNeural extends AgentAbstract<MazeVariables> implements AgentNeuralInterface<MazeVariables> {

    static final NetworkMemoryInterface<MazeVariables> MEMORY=new MazeNeuralValueMemory<>();
    private static final int START_X = 0, START_Y=0;
    public  static final double DISCOUNT_FACTOR=1, LEARNING_RATE=0.5;

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


    @Override
    public double readValue(StateInterface<MazeVariables> state) {
        return memory.read(state);
    }

    @SneakyThrows
    @Override
    public void writeValue(StateInterface<MazeVariables> state, double value) {
        throw new NoSuchMethodException();  //todo ISP
    }

    @Override
    public void learn(List<NstepExperience<MazeVariables>> miniBatch) {
        memory.learn(miniBatch);
    }


}
