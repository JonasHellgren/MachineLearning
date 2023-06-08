package multi_step_temp_diff.models;

import common.MathUtils;
import lombok.*;
import lombok.extern.java.Log;
import multi_step_temp_diff.environments.ForkEnvironment;
import multi_step_temp_diff.helpers.AgentHelper;
import multi_step_temp_diff.interfaces.AgentInterface;
import multi_step_temp_diff.interfaces.AgentNeuralInterface;
import multi_step_temp_diff.interfaces.EnvironmentInterface;
import multi_step_temp_diff.interfaces.NetworkMemoryInterface;

import java.util.List;

@Getter
@Setter
@Log
public abstract class AgentAbstract implements AgentInterface {
    EnvironmentInterface environment;
    int state;
    double discountFactor;
    AgentHelper helper;

    public AgentAbstract(@NonNull  EnvironmentInterface environment, int state, double discountFactor) {
        if (MathUtils.isZero(discountFactor)) {
            log.warning("Zero discountFactor");
        }

        this.environment = environment;
        this.state = state;
        this.discountFactor = discountFactor;
        this.helper = AgentHelper.builder()
                .nofActions(environment.actionSet().size())
                .environment(environment).discountFactor(discountFactor)
                .readFunction(this::readValue)
                .build();
    }

    @Override
    public int chooseAction(double probRandom) {
        return helper.chooseAction(probRandom,getState());
    }

    public int chooseRandomAction() {
        return helper.chooseRandomAction();
    }

    public int chooseBestAction(int state) {
        return helper.chooseBestAction(state);
    }

    @Override
    public void updateState(StepReturn stepReturn) {
        setState(stepReturn.newState);
    }

    public abstract double readValue(int state);

}
