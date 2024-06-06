package multi_agent_rl.domain.value_classes;

import com.google.common.base.Preconditions;
import multi_agent_rl.domain.abstract_classes.ActionAgent;
import multi_agent_rl.domain.abstract_classes.ActionJoint;
import multi_agent_rl.domain.abstract_classes.AgentI;
import multi_agent_rl.domain.abstract_classes.StateI;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AgentActions<V,O> {
    Map<String, ActionAgent> agentIdActionMap;

    public static <V,O> AgentActions<V,O> empty() {
        return new AgentActions<>();
    }

    public AgentActions() {
        agentIdActionMap=new HashMap<>();
    }

    public void addActions(List<AgentI<O>> agents, StateI<V,O> state) {
        agents.forEach(agent -> add(
                agent.getId(),agent.chooseAction(state.getObservation(agent.getId()))));
    }


    public void add(String id, ActionAgent actionAgent) {
        agentIdActionMap.put(id,actionAgent);
    }

    public ActionAgent get(String id) {
        Preconditions.checkArgument(agentIdActionMap.containsKey(id),"No such agent, id="+id);
        return agentIdActionMap.get(id);
    }

    public ActionJoint jointAction() {
        return  ActionJoint.ofInteger(agentIdActionMap.keySet().stream()
                .map(id -> agentIdActionMap.get(id).asInt()).toList());
    }

}
