package multi_step_temp_diff.domain.interfaces_and_abstract;

import common.MathUtils;
import lombok.*;
import lombok.extern.java.Log;
import multi_step_temp_diff.helpers.AgentActionSelector;
import multi_step_temp_diff.helpers.TemporalDifferenceTracker;
import multi_step_temp_diff.models.StepReturn;

@Getter
@Setter
@Log
public abstract class AgentAbstract<S> implements AgentInterface<S> {
    EnvironmentInterface<S> environment;
    StateInterface<S> state;
    double discountFactor;
    int nofSteps;
    AgentActionSelector<S> actionSelector;
    TemporalDifferenceTracker temporalDifferenceTracker;

    public AgentAbstract(@NonNull  EnvironmentInterface<S> environment,
                         StateInterface<S> state,
                         double discountFactor) {
        if (MathUtils.isZero(discountFactor)) {
            log.warning("Zero discountFactor");
        }

        this.environment = environment;
        this.state = state;
        this.discountFactor = discountFactor;
        this.nofSteps=0;
        this.actionSelector = AgentActionSelector.<S>builder()
                .nofActions(environment.actionSet().size())
                .environment(environment).discountFactor(discountFactor)
                .readFunction(this::readValue)
                .build();
        this.temporalDifferenceTracker=new TemporalDifferenceTracker();
    }

    @Override
    public int chooseAction(double probRandom) {
        nofSteps++;
        return actionSelector.chooseAction(probRandom,getState());
    }

    @Override
    public void clear() {
        nofSteps=0;
        temporalDifferenceTracker.reset();
    }

    @Override
    public void updateState(StepReturn<S> stepReturn) {
        setState(stepReturn.newState);
    }

    @Override
    public void storeTemporalDifference(double difference) {
        temporalDifferenceTracker.addDifference(Math.abs(difference));
    }

    @Override
    public abstract double readValue(StateInterface<S> state);

    public int chooseRandomAction() {
        return actionSelector.chooseRandomAction();
    }
    public int chooseBestAction(StateInterface<S> state) {
        return actionSelector.chooseBestAction(state);
    }

}
