package multi_step_temp_diff.models;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
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

@Builder
@Getter
public class AgentForkNeural implements AgentNeuralInterface {

    static final double DISCOUNT_FACTOR=1;
    private static final double LEARNING_RATE = 0.01;
    static final NetworkMemoryInterface<Integer> MEMORY=
            new ForkNeuralValueMemory<>(NetSettings.builder().build());
    private static final double VALUE_IF_NOT_PRESENT = 0;
    private static final int START_STATE = 0;

    private static final int NOF_STATES = ForkEnvironment.NOF_STATES;
    private static final int INPUT_SIZE = NOF_STATES;
    private static final int OUTPUT_SIZE = 1;
    private static final int NOF_NEURONS_HIDDEN = INPUT_SIZE;
    private static final double MARGIN = 1.0;

    @NonNull
    EnvironmentInterface environment;
    @Builder.Default
    int state= START_STATE;
    @Builder.Default
    NetworkMemoryInterface<Integer>  memory=MEMORY;
    @Builder.Default
    final double discountFactor=DISCOUNT_FACTOR;
    AgentHelper helper;

    public static AgentForkNeural newDefault() {

        NetSettings netSettings = NetSettings.builder()
                .inputSize(INPUT_SIZE).nofNeuronsHidden(NOF_NEURONS_HIDDEN)
                .minOut(ForkEnvironment.R_HELL).maxOut(ForkEnvironment.R_HEAVEN).build();

        return AgentForkNeural.builder()
                .environment(new ForkEnvironment())
                .memory(new ForkNeuralValueMemory<>(netSettings))
                .build();
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
        state = stepReturn.newState;
    }

    @Override
    public double readValue(int state) {
        return memory.read(state);
    }

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
