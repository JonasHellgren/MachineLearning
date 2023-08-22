package multi_step_temp_diff.domain.agent_abstract;

import multi_step_temp_diff.domain.agent_valueobj.AgentSettingsInterface;

public interface AgentTabularInterface<S> extends AgentInterface<S> {
     void writeValue(StateInterface<S> state, double value);
}
