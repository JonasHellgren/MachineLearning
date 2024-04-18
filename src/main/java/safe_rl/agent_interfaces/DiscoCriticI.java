package safe_rl.agent_interfaces;

import safe_rl.domain.abstract_classes.StateI;

public interface DiscoCriticI<V> {
    void fitCritic(double error);
    double readCritic();
    double readCritic(StateI<V> state);
}
