package policy_gradient_problems.agent_interfaces;

import policy_gradient_problems.abstract_classes.Action;
import policy_gradient_problems.abstract_classes.StateI;

/**
 * Generic top-level agent interface, handles ant state with variables V
 */

public interface AgentI<V> {
     Action chooseAction();
     StateI<V> getState();
     void setState(StateI<V> state);

}
