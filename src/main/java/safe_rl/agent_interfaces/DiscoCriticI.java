package safe_rl.agent_interfaces;

import safe_rl.domain.abstract_classes.StateI;

public interface DiscoCriticI<V> {
    void fitCritic(StateI<V> state, double error);
    double readCritic(StateI<V> state);
    double lossCriticLastUpdate();
}
