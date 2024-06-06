package multi_agent_rl.domain.abstract_classes;

/**
 * Generic top-level agent interface, handles a state with variables V
 */

public interface AgentI<O> {

    String getId();
    ActionAgent chooseAction(ObservationI<O> obs);
    double criticOut(ObservationI<O> obs);

//     ActionJoint chooseActionNominal(StateI<V> state);
 //    ActionJoint chooseActionNoExploration(StateI<V> state);
}
