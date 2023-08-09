package multi_step_temp_diff.domain.agent_abstract;

import common.MathUtils;
import lombok.*;
import lombok.extern.java.Log;
import multi_step_temp_diff.domain.agent_valueobj.AgentSettingsInterface;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.agent_parts.AgentActionSelector;
import multi_step_temp_diff.domain.helpers_common.ValueTracker;
import multi_step_temp_diff.domain.environment_abstract.StepReturn;

import java.util.DoubleSummaryStatistics;
import java.util.List;

@Getter
@Setter
@Log
public abstract class AgentAbstract<S> implements AgentInterface<S> {
    EnvironmentInterface<S> environment;
    StateInterface<S> state;
    //double discountFactor;
    AgentSettingsInterface agentSettings;
    int nofSteps;
    AgentActionSelector<S> actionSelector;
    ValueTracker temporalDifferenceTracker;
    protected ValueTracker errorHistory;

    public AgentAbstract(@NonNull  EnvironmentInterface<S> environment,
                         StateInterface<S> state,
                         AgentSettingsInterface agentSettings) {
        if (MathUtils.isZero(agentSettings.discountFactor())) {
            log.warning("Zero discountFactor");
        }

        this.environment = environment;
        this.state = state;
        this.agentSettings = agentSettings;
        this.nofSteps=0;
        this.actionSelector = AgentActionSelector.<S>builder()
                .nofActions(environment.actionSet().size())
                .environment(environment).discountFactor(agentSettings.discountFactor())  //todo agentSettings
                .readMemoryFunction(this::readValue)
                .build();
        this.temporalDifferenceTracker=new ValueTracker();
        errorHistory=new ValueTracker();

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
        errorHistory.reset();
    }

    @Override
    public void updateState(StepReturn<S> stepReturn) {
        setState(stepReturn.newState);
    }

    @Override
    public void storeTemporalDifference(double difference) {
        temporalDifferenceTracker.addValue(Math.abs(difference));
    }

    @Override
    public abstract double readValue(StateInterface<S> state);

    public int chooseRandomAction() {
        return actionSelector.chooseRandomAction();
    }
    public int chooseBestAction(StateInterface<S> state) {
        return actionSelector.chooseBestAction(state);
    }

    protected void addErrorsToHistory(List<Double> errors) {
        DoubleSummaryStatistics sumStats=errors.stream().mapToDouble(v -> v).summaryStatistics();
        errorHistory.addValue(sumStats.getAverage());
    }

}
