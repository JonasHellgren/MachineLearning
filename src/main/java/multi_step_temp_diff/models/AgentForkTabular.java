package multi_step_temp_diff.models;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import multi_step_temp_diff.environments.ForkEnvironment;
import multi_step_temp_diff.helpers.AgentHelper;
import multi_step_temp_diff.interfaces.AgentInterface;
import multi_step_temp_diff.interfaces.EnvironmentInterface;
import java.util.*;

@Builder
@Getter
public class AgentForkTabular implements AgentInterface {

    static final double DISCOUNT_FACTOR = 1;
    static final Map<Integer, Double> MEMORY = new HashMap<>();
    private static final double VALUE_IF_NOT_PRESENT = 0;
    private static final int START_STATE = 0;

    @NonNull EnvironmentInterface environment;
    @Builder.Default
    int state = START_STATE;
    @Builder.Default
    Map<Integer, Double> memory = MEMORY;
    @Builder.Default
    final double discountFactor = DISCOUNT_FACTOR;
    AgentHelper helper;

    public static AgentForkTabular newDefault() {
        return AgentForkTabular.builder()
                .environment(new ForkEnvironment())
                .build();
    }

    @Override
    public int getState() {
        return state;
    }

    @Override
    public double getDiscountFactor() {
        return discountFactor;
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
        return memory.getOrDefault(state, VALUE_IF_NOT_PRESENT);
    }


    public void writeValue(int state, double value) {
        memory.put(state, value);
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
