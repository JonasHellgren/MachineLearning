package multi_step_temp_diff.interfaces_and_abstract;

public interface AgentTabularInterface<S> extends AgentInterface<S> {
     void writeValue(StateInterface<S>  state, double value);

}
