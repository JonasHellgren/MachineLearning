package multi_step_temp_diff.agents;

import lombok.Builder;
import multi_step_temp_diff.environments.ForkState;
import multi_step_temp_diff.environments.MazeVariables;
import multi_step_temp_diff.environments.MazeState;
import multi_step_temp_diff.environments.MazeVariables;
import multi_step_temp_diff.interfaces_and_abstract.*;

import java.util.HashMap;
import java.util.Map;

public class AgentMazeTabular extends AgentAbstract<MazeVariables> implements AgentTabularInterface<MazeVariables> {

    static final double DISCOUNT_FACTOR = 1;
    static final Map<MazeState, Double> MEMORY = new HashMap<>();
    public static final double VALUE_IF_NOT_PRESENT = 0;
    private static final int START_X=0, START_Y = 0;

    Map<MazeState, Double> memory;

    @Builder
    private AgentMazeTabular(EnvironmentInterface<MazeVariables> environment,
                             StateInterface<MazeVariables> state,
                             double discountFactor) {
        super(environment,state,discountFactor);
        this.memory = MEMORY;
    }

    public static AgentMazeTabular newDefault(EnvironmentInterface<MazeVariables> environment) {
        return AgentMazeTabular.newWithDiscountFactor(environment,DISCOUNT_FACTOR);
    }

    public static AgentMazeTabular newWithDiscountFactor(EnvironmentInterface<MazeVariables> environment,
                                                         double discountFactor) {
        return AgentMazeTabular.builder()
                .environment(environment).state(new MazeState(MazeVariables.newFromXY(START_X,START_Y)))
                .discountFactor(discountFactor).build();
    }

    public static AgentMazeTabular newWithStartState(EnvironmentInterface<MazeVariables> environment,
                                                     StateInterface<MazeVariables>  startState) {
        return AgentMazeTabular.builder()
                .environment(environment).state(startState).discountFactor(DISCOUNT_FACTOR).build();
    }

    @Override
    public double readValue(StateInterface<MazeVariables>  state) {
        return memory.getOrDefault((MazeState) state, VALUE_IF_NOT_PRESENT);
    }

    @Override
    public void writeValue(StateInterface<MazeVariables>  state, double value) {
        memory.put((MazeState) state, value);
    }

    public void clear() {
        super.clear();
        memory.clear();
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
