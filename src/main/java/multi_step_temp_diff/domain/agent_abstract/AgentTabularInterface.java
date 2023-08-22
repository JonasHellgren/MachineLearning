package multi_step_temp_diff.domain.agent_abstract;

public interface AgentTabularInterface<S> extends AgentInterface<S> {
     void writeValue(StateInterface<S> state, double value);
}
