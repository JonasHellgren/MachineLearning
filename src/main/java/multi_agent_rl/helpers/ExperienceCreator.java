package multi_agent_rl.helpers;


import com.google.common.collect.Lists;
import lombok.Builder;
import multi_agent_rl.domain.abstract_classes.*;
import multi_agent_rl.domain.value_classes.*;
import java.util.List;

@Builder
public class ExperienceCreator<V> {
    public static final int VALUE_DUMMY = 0;
    EnvironmentI<V> environment;
    TrainerParameters parameters;

    public List<Experience<V>> getExperiences(List<AgentI<V>> agents, StateI<V> stateStart) {
        List<Experience<V>> experienceList = Lists.newArrayList();
        int si = 0;
        StepReturn<V> sr;
        var state=stateStart.copy();
        do {
            StateI<V> finalState = state;
            List<ActionAgent> actions=agents.stream().map(a -> a.chooseAction(finalState)).toList();
            ActionJoint action=ActionJoint.ofInteger(actions.stream().map(a -> a.asInt()).toList());
            sr = environment.step(state, action);
            experienceList.add(createExperience(state,action, sr));
            si++;
            state=sr.state();
        } while (isNotTerminalAndNofStepsNotExceeded(si, sr));
        return experienceList;
    }

    private Experience<V> createExperience(StateI<V> state,
                                           ActionJoint action,
                                           StepReturn<V> sr) {
        return Experience.<V>builder()
                .state(state)
                .action(action)
                .reward(sr.reward())
                .stateNew(sr.state())
                .value(VALUE_DUMMY)
                .build();
    }

    boolean isNotTerminalAndNofStepsNotExceeded(int si, StepReturn<V> sr) {
        return !sr.isTerminal() && si < parameters.nofStepsMax();
    }

}
