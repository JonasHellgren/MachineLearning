package safe_rl.agent_interfaces;

import safe_rl.domain.abstract_classes.StateI;
import safe_rl.domain.memories.DisCoMemory;

public interface DiscoCriticI<V> {
    DisCoMemory<V> getCritic();
    void fitCritic(StateI<V> state, double error);
    double readCritic(StateI<V> state);
    double lossCriticLastUpdates();
    void clearCriticLosses();
}
