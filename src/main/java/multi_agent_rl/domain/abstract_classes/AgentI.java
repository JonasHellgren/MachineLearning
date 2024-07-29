package multi_agent_rl.domain.abstract_classes;

/**
 * Generic top-level agent interface, handles a stateNew with variables V
 */

public interface AgentI<O> {

    String getId();
    ActionAgent chooseAction(ObservationI<O> obs);
    double criticOut(ObservationI<O> obs);

//     ActionJoint chooseActionNominal(StateI<V> stateNew);
 //    ActionJoint chooseActionNoExploration(StateI<V> stateNew);
}
