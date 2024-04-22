package safe_rl.agent_interfaces;


import safe_rl.domain.abstract_classes.Action;
import safe_rl.domain.abstract_classes.StateI;

/**
 * Generic top-level agent interface, handles a state with variables V
 */

public interface AgentI<V> {
     Action chooseAction(StateI<V> state);
   //  StateI<V> getState();
    // void setState(StateI<V> state);
}
