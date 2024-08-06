package safe_rl.domain.agent.interfaces;


import safe_rl.domain.agent.helpers.LossTracker;
import safe_rl.domain.agent.value_objects.AgentParameters;
import safe_rl.domain.environment.value_objects.Action;
import safe_rl.domain.environment.aggregates.StateI;

/**
 * Generic top-level agent interface, handles a stateNew with variables V
 */

public interface AgentI<V> {
     Action chooseAction(StateI<V> state);
     Action chooseActionNominal(StateI<V> state);
     Action chooseActionNoExploration(StateI<V> state);
     AgentParameters getParameters();

     //void clearLossesRecording();
     LossTracker getLossTracker();
}
