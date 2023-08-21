package multi_step_temp_diff.domain.helpers_common;

import multi_step_temp_diff.domain.agent_abstract.AgentInterface;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class StateValuePrinter<S> {

    AgentInterface<S> agent;
    EnvironmentInterface<S> environment;

    public StateValuePrinter(AgentInterface<S> agent, EnvironmentInterface<S> environment) {
        this.agent = agent;
        this.environment = environment;
    }

    public  void printStateValues() {
        printStateValues(environment.stateSet());
    }

    public  void printStateValues(Set<StateInterface<S>> stateSet) {
        Map<StateInterface<S>, Double> stateValues = getStateValueMap(stateSet);

        DecimalFormat formatter = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.US)); //US <=> only dots
        stateValues.forEach((s,v) -> System.out.println("s="+s+", v="+formatter.format(v)));
    }

    public  Map<StateInterface<S>, Double> getStateValueMap(Set<StateInterface<S>> stateSet) {
        Map<StateInterface<S>,Double> stateValues=new HashMap<>();
        for (StateInterface<S> state: stateSet) {
            stateValues.put(state, agent.readValue(state));
        }
        return stateValues;
    }

}
