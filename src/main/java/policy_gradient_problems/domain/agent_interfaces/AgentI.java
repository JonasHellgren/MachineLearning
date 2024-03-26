package policy_gradient_problems.domain.agent_interfaces;

import policy_gradient_problems.domain.abstract_classes.Action;
import policy_gradient_problems.domain.abstract_classes.StateI;

import java.util.List;

/**
 * Generic top-level agent interface, handles a state with variables V
 */

public interface AgentI<V> {
     List<Double> getActionProbabilities();
     Action chooseAction();
     StateI<V> getState();
     void setState(StateI<V> state);
}
