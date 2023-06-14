package multi_step_temp_diff.models;

import lombok.Builder;
import lombok.Getter;
import lombok.SneakyThrows;
import multi_step_temp_diff.environments.ForkState;
import multi_step_temp_diff.environments.ForkVariables;
import multi_step_temp_diff.interfaces_and_abstract.*;
import multi_step_temp_diff.memory.ForkNeuralValueMemory;

import java.util.*;

/**
 *
 * http://www.incompleteideas.net/papers/fernando-sutton-2019.pdf
 * https://arxiv.org/abs/1810.09967
 * https://openreview.net/pdf?id=X6YPReSv5CX
 * https://arxiv.org/pdf/1602.04621.pdf
 *
 * https://www.baeldung.com/lombok-builder-inheritance
 *
 */

@Getter
public class AgentForkNeural extends AgentAbstract<ForkVariables> implements AgentNeuralInterface<ForkVariables> {

    static final NetworkMemoryInterface<ForkVariables> MEMORY=new ForkNeuralValueMemory<>();
    private static final int START_STATE = 0;
    static final double DISCOUNT_FACTOR=1;

    NetworkMemoryInterface<ForkVariables>  memory;


    @Builder
    public AgentForkNeural(EnvironmentInterface<ForkVariables> environment,
                           StateInterface<ForkVariables> state,
                           double discountFactor,
                           NetworkMemoryInterface<ForkVariables> memory) {
        super(environment,state,discountFactor);
        this.memory = memory;
    }

    public static AgentForkNeural newDefault(EnvironmentInterface<ForkVariables> environment) {
        return AgentForkNeural.newWithDiscountFactor(environment,DISCOUNT_FACTOR);
    }

    public static AgentForkNeural newWithDiscountFactor(EnvironmentInterface<ForkVariables> environment,double discountFactor) {
        return AgentForkNeural.builder()
                .environment(environment)
                .state(new ForkState(ForkVariables.newFromPos(START_STATE))).discountFactor(discountFactor)
                .memory(MEMORY).build();
    }


    @Override
    public double readValue(StateInterface<ForkVariables> state) {
        return memory.read(state);
    }

    @SneakyThrows
    @Override
    public void writeValue(StateInterface<ForkVariables> state, double value) {
        throw new NoSuchMethodException();  //todo ISP
    }

    @Override
    public void learn(List<NstepExperience<ForkVariables>> miniBatch) {
        memory.learn(miniBatch);
    }


}
