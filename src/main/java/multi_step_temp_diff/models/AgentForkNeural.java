package multi_step_temp_diff.models;

import lombok.Builder;
import lombok.Getter;
import multi_step_temp_diff.environments.ForkEnvironment;
import multi_step_temp_diff.helpers.AgentHelper;
import multi_step_temp_diff.interfaces.EnvironmentInterface;
import multi_step_temp_diff.interfaces.AgentNeuralInterface;
import multi_step_temp_diff.interfaces.NetworkMemoryInterface;
import multi_step_temp_diff.memory.ForkNeuralValueMemory;

import java.util.*;

/**
 *
 * http://www.incompleteideas.net/papers/fernando-sutton-2019.pdf
 * https://arxiv.org/abs/1810.09967
 * https://openreview.net/pdf?id=X6YPReSv5CX
 * https://arxiv.org/pdf/1602.04621.pdf
 */

@Getter
public class AgentForkNeural extends AgentAbstract {

    static final NetworkMemoryInterface<Integer> MEMORY=
            new ForkNeuralValueMemory<>();
    private static final int START_STATE = 0;
    static final double DISCOUNT_FACTOR=1;

    NetworkMemoryInterface<Integer>  memory=MEMORY;


    @Builder
    public AgentForkNeural(EnvironmentInterface environment, int state, double discountFactor, NetworkMemoryInterface<Integer> memory) {
        super(environment,state,discountFactor,null);
        this.memory = MEMORY;
    }

    public static AgentForkNeural newDefault(EnvironmentInterface environment) {
        return AgentForkNeural.builder().environment(environment).build();
    }

    public static AgentForkNeural newWithDiscountFactor(EnvironmentInterface environment,double discountFactor) {
        return AgentForkNeural.builder().environment(environment).discountFactor(discountFactor).build();
    }

    @Override
    public void setState(int state) {
        this.state = state;
    }

    @Override
    public int chooseAction(double probRandom) {
        lazyInitHelper();
        return helper.chooseAction(probRandom,getState());
    }

    @Override
    public int chooseRandomAction() {
        lazyInitHelper();
        return helper.chooseRandomAction();
    }

    @Override
    public int chooseBestAction(int state) {
        lazyInitHelper();
        return helper.chooseBestAction(state);
    }

    @Override
    public void updateState(StepReturn stepReturn) {
        setState(stepReturn.newState);
    }

    @Override
    public double readValue(int state) {
        return memory.read(state);
    }

    @Override
    public void learn(List<NstepExperience> miniBatch) {
        memory.learn(miniBatch);
    }

    private void lazyInitHelper() {
        if (Objects.isNull(helper)) {
            helper = AgentHelper.builder()
                    .nofActions(ForkEnvironment.NOF_ACTIONS)
                    .environment(environment).discountFactor(discountFactor)
                    .readFunction(this::readValue)
                    .build();
        }
    }

}
