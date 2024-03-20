package policy_gradient_problems.domain.agent_interfaces;

import policy_gradient_problems.domain.abstract_classes.Action;
import policy_gradient_problems.domain.abstract_classes.StateI;

/**
 * Generic top-level agent interface, handles ant state with variables V
 */

public interface AgentI<V> {
     Action chooseAction();
     StateI<V> getState();
     void setState(StateI<V> state);
}
