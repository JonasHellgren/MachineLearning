package multi_agent_rl.helpers;


import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import multi_agent_rl.domain.abstract_classes.*;
import multi_agent_rl.domain.value_classes.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
  /*          List<ActionAgent> actions=agents.stream()
                    .map(a -> a.chooseAction(finalState.getObservation(a.getId()))).toList();
  */
            AgentActions<V,O> agentActions=AgentActions.empty();
            agentActions.addActions(agents,state);

//            ActionJoint action=ActionJoint.ofInteger(actions.stream().map(a -> a.asInt()).toList());

            ActionJoint action=agentActions.jointAction();

            sr = environment.step(state, action);
            experienceList.add(createExperience(state,action,sr,agentActions));
            si++;
            state=sr.state();
        } while (isNotTerminalAndNofStepsNotExceeded(si, sr));
        return experienceList;
    }

    private Experience<V,O> createExperience(StateI<V,O> state,
                                           ActionJoint action,
                                           StepReturn<V,O> sr,
                                          AgentActions<V,O> agentActions
                                             ) {
        return Experience.<V,O>builder()
                .state(state)
                .action(action)
                .reward(sr.reward())
                .stateNew(sr.state())
                .agentActions(agentActions)
                .isTerminal(sr.isTerminal())
                .value(VALUE_DUMMY)
                .build();
    }

    boolean isNotTerminalAndNofStepsNotExceeded(int si, StepReturn<V,O> sr) {
        return !sr.isTerminal() && si < parameters.nofStepsMax();
    }

}
