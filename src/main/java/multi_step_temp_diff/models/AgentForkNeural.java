package multi_step_temp_diff.models;

import lombok.Builder;
import lombok.Getter;
import multi_step_temp_diff.interfaces_and_abstract.AgentAbstract;
import multi_step_temp_diff.interfaces_and_abstract.EnvironmentInterface;
import multi_step_temp_diff.interfaces_and_abstract.AgentNeuralInterface;
import multi_step_temp_diff.interfaces_and_abstract.NetworkMemoryInterface;
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
public class AgentForkNeural extends AgentAbstract implements AgentNeuralInterface {

    static final NetworkMemoryInterface<Integer> MEMORY=new ForkNeuralValueMemory<>();
    private static final int START_STATE = 0;
    static final double DISCOUNT_FACTOR=1;

    NetworkMemoryInterface<Integer>  memory;


    @Builder
    public AgentForkNeural(EnvironmentInterface environment,
                           int state,
                           double discountFactor,
                           NetworkMemoryInterface<Integer> memory) {
        super(environment,state,discountFactor);
        this.memory = memory;
    }

    public static AgentForkNeural newDefault(EnvironmentInterface environment) {
        return AgentForkNeural.newWithDiscountFactor(environment,DISCOUNT_FACTOR);
    }

    public static AgentForkNeural newWithDiscountFactor(EnvironmentInterface environment,double discountFactor) {
        return AgentForkNeural.builder()
                .environment(environment).state(START_STATE).discountFactor(discountFactor).memory(MEMORY).build();
    }


    @Override
    public double readValue(int state) {
        return memory.read(state);
    }

    @Override
    public void learn(List<NstepExperience> miniBatch) {
        memory.learn(miniBatch);
    }


}
