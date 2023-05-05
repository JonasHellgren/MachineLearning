package multi_step_temp_diff.helpers;

import multi_step_temp_diff.interfaces.AgentInterface;
import org.apache.arrow.flatbuf.Int;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AgentInfo {

    AgentInterface agent;

    public AgentInfo(AgentInterface agent) {
        this.agent = agent;
    }

    public Map<Integer,Double> stateValueMap(Set<Integer> stateSet) {
        Map<Integer,Double> map=new HashMap<>();
        for (Integer state:stateSet) {
            map.put(state, agent.readValue(state));
        }
        return map;
    }

}
