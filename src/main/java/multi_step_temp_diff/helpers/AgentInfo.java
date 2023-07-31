package multi_step_temp_diff.helpers;

import common.MovingAverage;
import multi_step_temp_diff.domain.interfaces_and_abstract.AgentAbstract;
import multi_step_temp_diff.domain.interfaces_and_abstract.AgentInterface;
import multi_step_temp_diff.domain.interfaces_and_abstract.NetworkMemoryInterface;
import multi_step_temp_diff.domain.interfaces_and_abstract.StateInterface;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AgentInfo<S> {

    AgentInterface<S> agent;
    AgentAbstract<S> agentCasted;

    public AgentInfo(AgentInterface<S> agent) {
        this.agent = agent;
        this.agentCasted=(AgentAbstract<S>) agent;
    }

    public Map<StateInterface<S>,Double> stateValueMap(Set<StateInterface<S>> stateSet) {
        Map<StateInterface<S>,Double> map=new HashMap<>();
        for (StateInterface<S> state:stateSet) {
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

    public TemporalDifferenceTracker getTemporalDifferenceTracker() {
        return agentCasted.getTemporalDifferenceTracker();
    }


    public Map<StateInterface<S>,Double> getStateValues(NetworkMemoryInterface<S> memory,
                                                        Set<StateInterface<S>> stateSet) {
        Map<StateInterface<S>,Double> stateValues=new HashMap<>();
        for (StateInterface<S> state:stateSet) {
            stateValues.put(state,memory.read(state));
        }
        return stateValues;
    }

}
