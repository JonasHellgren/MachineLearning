package policy_gradient_problems.abstract_classes;

import lombok.Setter;

@Setter
public abstract class AgentAbstract<V> implements AgentInterface<V> {

    StateInterface<V> state;

}
