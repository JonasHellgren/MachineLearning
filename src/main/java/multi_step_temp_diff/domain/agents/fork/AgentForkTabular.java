package multi_step_temp_diff.domain.agents.fork;

import lombok.Builder;
import lombok.Getter;
import multi_step_temp_diff.domain.agent_abstract.AgentAbstract;
import multi_step_temp_diff.domain.agent_abstract.AgentTabularInterface;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.environments.fork.ForkState;
import multi_step_temp_diff.domain.environments.fork.ForkVariables;

import java.util.*;

@Getter
public class AgentForkTabular extends AgentAbstract<ForkVariables> implements AgentTabularInterface<ForkVariables> {

    static final double DISCOUNT_FACTOR = 1;
    static final Map<Integer, Double> MEMORY = new HashMap<>();
    private static final double VALUE_IF_NOT_PRESENT = 0;
    private static final int START_STATE = 0;

    Map<Integer, Double> memory;

    @Builder
    private AgentForkTabular(EnvironmentInterface<ForkVariables> environment,
                             StateInterface<ForkVariables> state,
                             double discountFactor) {
        super(environment,state,discountFactor);
        this.memory = MEMORY;
    }

    public static AgentForkTabular newDefault(EnvironmentInterface<ForkVariables> environment) {
        return AgentForkTabular.newWithDiscountFactor(environment,DISCOUNT_FACTOR);
    }

    public static AgentForkTabular newWithDiscountFactor(EnvironmentInterface<ForkVariables> environment,
                                                         double discountFactor) {
        return AgentForkTabular.builder()
                .environment(environment).state(new ForkState(ForkVariables.newFromPos(START_STATE)))
                .discountFactor(discountFactor).build();
    }

    public static AgentForkTabular newWithStartState(EnvironmentInterface<ForkVariables> environment,
                                                     StateInterface<ForkVariables>  startState) {
        return AgentForkTabular.builder()
                .environment(environment).state(startState).discountFactor(DISCOUNT_FACTOR).build();
    }

    @Override
    public double readValue(StateInterface<ForkVariables>  state) {
        return memory.getOrDefault(state.getVariables().position, VALUE_IF_NOT_PRESENT);
    }

    @Override
    public void writeValue(StateInterface<ForkVariables>  state, double value) {
        memory.put(state.getVariables().position, value);
    }

    public void clear() {
        super.clear();
        memory.clear();
    }


}
