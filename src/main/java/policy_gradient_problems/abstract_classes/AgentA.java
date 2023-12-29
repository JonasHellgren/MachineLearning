package policy_gradient_problems.abstract_classes;

import lombok.Setter;

@Setter
public abstract class AgentA<V> implements AgentI<V> {

    StateI<V> state;

}
