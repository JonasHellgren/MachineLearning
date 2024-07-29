package safe_rl.domain.agent.interfaces;

import safe_rl.domain.environment.aggregates.StateI;
import safe_rl.domain.agent.aggregates.DisCoMemory;

public interface DiscoCriticI<V> {
    DisCoMemory<V> getCritic();
    void fitCritic(StateI<V> state, double error);
    double readCritic(StateI<V> state);
    double lossCriticLastUpdates();
    void clearCriticLosses();
}
