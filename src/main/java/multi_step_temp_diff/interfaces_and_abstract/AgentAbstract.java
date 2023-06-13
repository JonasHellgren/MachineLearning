package multi_step_temp_diff.interfaces_and_abstract;

import common.MathUtils;
import lombok.*;
import lombok.extern.java.Log;
import multi_step_temp_diff.helpers.AgentActionSelector;
import multi_step_temp_diff.models.StepReturn;

@Getter
@Setter
@Log
public abstract class AgentAbstract implements AgentInterface {
    EnvironmentInterface environment;
    int state;
    double discountFactor;
    AgentActionSelector actionSelector;

    public AgentAbstract(@NonNull  EnvironmentInterface environment, int state, double discountFactor) {
        if (MathUtils.isZero(discountFactor)) {
            log.warning("Zero discountFactor");
        }

        this.environment = environment;
        this.state = state;
        this.discountFactor = discountFactor;
        this.actionSelector = AgentActionSelector.builder()
                .nofActions(environment.actionSet().size())
                .environment(environment).discountFactor(discountFactor)
                .readFunction(this::readValue)
                .build();
    }

    @Override
    public int chooseAction(double probRandom) {
        return actionSelector.chooseAction(probRandom,getState());
    }

    public int chooseRandomAction() {
        return actionSelector.chooseRandomAction();
    }

    public int chooseBestAction(int state) {
        return actionSelector.chooseBestAction(state);
    }

    @Override
    public void updateState(StepReturn stepReturn) {
        setState(stepReturn.newState);
    }

    public abstract double readValue(int state);

}
