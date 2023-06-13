package multi_step_temp_diff.helpers;

import common.MovingAverage;
import multi_step_temp_diff.interfaces_and_abstract.AgentAbstract;
import multi_step_temp_diff.interfaces_and_abstract.AgentInterface;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AgentInfo {

    AgentInterface agent;
    AgentAbstract agentCasted;

    public AgentInfo(AgentInterface agent) {
        this.agent = agent;
        this.agentCasted=(AgentAbstract) agent;

    }

    public Map<Integer,Double> stateValueMap(Set<Integer> stateSet) {
        Map<Integer,Double> map=new HashMap<>();
        for (Integer state:stateSet) {
            map.put(state, agent.readValue(state));
        }
        return map;
    }

    public double getDiscountFactor() {
        return agentCasted.getDiscountFactor();
    }

    public int getNofSteps() {
        return agentCasted.getNofSteps();
    }

    public List<Double> getFilteredTemporalDifferenceList(int lengthWindow) {
        List<Double> diffList=agentCasted.getTemporalDifferenceTracker().getTemporalDifferenceList();
        MovingAverage movingAverage=new MovingAverage(lengthWindow,diffList);
        return movingAverage.getFiltered();

    }

}
