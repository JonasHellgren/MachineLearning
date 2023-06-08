package multi_step_temp_diff.models;

import lombok.Builder;
import lombok.Getter;
import multi_step_temp_diff.interfaces_and_abstract.AgentAbstract;
import multi_step_temp_diff.interfaces_and_abstract.AgentInterface;
import multi_step_temp_diff.interfaces_and_abstract.EnvironmentInterface;

import java.util.*;

@Getter
public class AgentForkTabular extends AgentAbstract implements AgentInterface {

    static final double DISCOUNT_FACTOR = 1;
    static final Map<Integer, Double> MEMORY = new HashMap<>();
    private static final double VALUE_IF_NOT_PRESENT = 0;
    private static final int START_STATE = 0;

    Map<Integer, Double> memory;

    @Builder
    private AgentForkTabular(EnvironmentInterface environment,
                           int state,
                           double discountFactor) {
        super(environment,state,discountFactor);
        this.memory = MEMORY;
    }

    public static AgentForkTabular newDefault(EnvironmentInterface environment) {
        return AgentForkTabular.newWithDiscountFactor(environment,DISCOUNT_FACTOR);
    }

    public static AgentForkTabular newWithDiscountFactor(EnvironmentInterface environment,double discountFactor) {
        return AgentForkTabular.builder()
                .environment(environment).state(START_STATE).discountFactor(discountFactor).build();
    }

    public static AgentForkTabular newWithStartState(EnvironmentInterface environment,int startState) {
        return AgentForkTabular.builder()
                .environment(environment).state(startState).discountFactor(DISCOUNT_FACTOR).build();
    }

    @Override
    public double readValue(int state) {
        return memory.getOrDefault(state, VALUE_IF_NOT_PRESENT);
    }


    public void writeValue(int state, double value) {
        memory.put(state, value);
    }


}
