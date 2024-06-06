package multi_agent_rl.helpers;


import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import multi_agent_rl.domain.abstract_classes.*;
import multi_agent_rl.domain.value_classes.*;
import java.util.List;

@AllArgsConstructor
public class ExperienceCreator<V,O> {
    public static final int VALUE_DUMMY = 0;
    EnvironmentI<V,O> environment;
    TrainerParameters parameters;

    public List<Experience<V,O>> getExperiences(List<AgentI<O>> agents, StateI<V,O> stateStart) {
        List<Experience<V,O>> experienceList = Lists.newArrayList();
        int si = 0;
        StepReturn<V,O> sr;
        var state=stateStart.copy();
        do {
            StateI<V,O> finalState = state;
            List<ActionAgent> actions=agents.stream()
                    .map(a -> a.chooseAction(finalState.getObservation(a.getId()))).toList();
            ActionJoint action=ActionJoint.ofInteger(actions.stream().map(a -> a.asInt()).toList());
            sr = environment.step(state, action);
            experienceList.add(createExperience(state,action, sr));
            si++;
            state=sr.state();
        } while (isNotTerminalAndNofStepsNotExceeded(si, sr));
        return experienceList;
    }

    private Experience<V,O> createExperience(StateI<V,O> state,
                                           ActionJoint action,
                                           StepReturn<V,O> sr) {
        return Experience.<V,O>builder()
                .state(state)
                .action(action)
                .reward(sr.reward())
                .stateNew(sr.state())
                .isTerminal(sr.isTerminal())
                .value(VALUE_DUMMY)
                .build();
    }

    boolean isNotTerminalAndNofStepsNotExceeded(int si, StepReturn<V,O> sr) {
        return !sr.isTerminal() && si < parameters.nofStepsMax();
    }

}
