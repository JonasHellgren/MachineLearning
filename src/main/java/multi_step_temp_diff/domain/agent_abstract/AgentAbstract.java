package multi_step_temp_diff.domain.agent_abstract;

import common.MathUtils;
import lombok.*;
import lombok.extern.java.Log;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.agent_parts.action_selector.AgentActionSelector;
import multi_step_temp_diff.domain.helpers_common.ValueTracker;
import multi_step_temp_diff.domain.environment_abstract.StepReturn;

import java.util.DoubleSummaryStatistics;
import java.util.List;

/***
 *  The concrete implementation of this class is adapted for an environment
 *  To avoid code duplication in concrete classes, this class includes fields such as an environment reference
 *  The method readValue is abstract and reads the state value approximation from memory
 *
 */

@Getter
@Setter
@Log
public abstract class AgentAbstract<S> implements AgentInterface<S> {
    EnvironmentInterface<S> environment;
    StateInterface<S> state;
    AgentSettingsInterface agentSettings;
    AgentActionSelector<S> actionSelector;
    ValueTracker temporalDifferenceTracker;
    protected ValueTracker errorMemoryTracker;

    public AgentAbstract(@NonNull  EnvironmentInterface<S> environment,
                         StateInterface<S> state,
                         AgentSettingsInterface agentSettings) {
        if (MathUtils.isZero(agentSettings.discountFactor())) {
            log.warning("Zero discountFactor");
        }

        this.environment = environment;
        this.state = state;
        this.agentSettings = agentSettings;
        this.actionSelector = AgentActionSelector.<S>builder()
                .nofActions(environment.actionSet().size())
                .environment(environment).agentSettings(agentSettings)
                .readMemoryFunction(this::readValue)
                .build();
        this.temporalDifferenceTracker=new ValueTracker();
        this.errorMemoryTracker =new ValueTracker();
    }

    @Override
    public int chooseAction(double probRandom) {
        return actionSelector.chooseAction(probRandom,getState());
    }

    @Override
    public void clear() {
        temporalDifferenceTracker.reset();
        errorMemoryTracker.reset();
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
        errorMemoryTracker.addValue(sumStats.getAverage());
    }

}
