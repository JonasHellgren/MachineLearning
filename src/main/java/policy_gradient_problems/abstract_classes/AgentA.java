package policy_gradient_problems.abstract_classes;

import lombok.AllArgsConstructor;
import lombok.Setter;

@Setter
@AllArgsConstructor
public abstract class AgentA<V> implements AgentI<V> {

    StateI<V> state;

}
