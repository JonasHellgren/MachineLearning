package multi_agent_rl.domain.abstract_classes;

/**
 * Generic top-level agent interface, handles a state with variables V
 */

public interface AgentI<V> {
     ActionAgent chooseAction(StateI<V> state);
//     ActionJoint chooseActionNominal(StateI<V> state);
 //    ActionJoint chooseActionNoExploration(StateI<V> state);
}
