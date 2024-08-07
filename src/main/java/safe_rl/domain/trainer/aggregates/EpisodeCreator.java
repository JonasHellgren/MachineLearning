package safe_rl.domain.trainer.aggregates;


import com.beust.jcommander.internal.Lists;
import com.joptimizer.exception.JOptimizerException;
import lombok.Builder;
import safe_rl.domain.agent.interfaces.AgentI;
import safe_rl.domain.environment.value_objects.Action;
import safe_rl.domain.environment.EnvironmentI;
import safe_rl.domain.environment.aggregates.StateI;
import safe_rl.domain.trainer.value_objects.ActionRewardStateNew;
import safe_rl.domain.trainer.value_objects.Experience;
import safe_rl.domain.trainer.value_objects.TrainerParameters;
import safe_rl.domain.environment.value_objects.StepReturn;
import safe_rl.domain.safety_layer.SafetyLayer;

import java.util.List;
import java.util.Optional;

@Builder
public class EpisodeCreator<V> {
    public static final int VALUE_DUMMY = 0;
    EnvironmentI<V> environment;
    TrainerParameters parameters;
    SafetyLayer<V> safetyLayer;

    public List<Experience<V>> getExperiences(AgentI<V> agent, StateI<V> stateStart) throws JOptimizerException {
        List<Experience<V>> experienceList = Lists.newArrayList();
        int si = 0;
        StepReturn<V> sr;
        var state=stateStart.copy();
        do {
            var action = agent.chooseAction(state);
            var actionMaybeCorrected = safetyLayer.correctAction(state, action);
            sr = environment.step(state, actionMaybeCorrected);
            experienceList.add(createExperience(state, sr, action, actionMaybeCorrected));
            si++;
            state=sr.state();
        } while (isNotTerminalAndNofStepsNotExceeded(si, sr));
        return experienceList;
    }

    private Experience<V> createExperience(StateI<V> state,
                                           StepReturn<V> sr,
                                           Action action, Action actionMaybeCorrected) {

        var ars = actionMaybeCorrected.isSafeCorrected()
                ? ActionRewardStateNew.<V>ofAction(action)
                : ActionRewardStateNew.<V>builder()
                .action(action).reward(sr.reward()).stateNext(sr.state()).isTerminal(sr.isTerminal()).build();

        var arsCorrected = actionMaybeCorrected.isSafeCorrected()
                ? Optional.of(ActionRewardStateNew.<V>builder()
                .action(actionMaybeCorrected).reward(sr.reward()).stateNext(sr.state())
                .isTerminal(sr.isTerminal()).build())
                : Optional.<ActionRewardStateNew<V>>empty();  //a bit tricky generics

        return Experience.<V>builder()
                .state(state)
                .ars(ars)
                .arsCorrected(arsCorrected)
                .value(VALUE_DUMMY)
                .build();
    }

    boolean isNotTerminalAndNofStepsNotExceeded(int si, StepReturn<V> sr) {
        return !sr.isTerminal() && si < parameters.nofStepsMax();
    }

}