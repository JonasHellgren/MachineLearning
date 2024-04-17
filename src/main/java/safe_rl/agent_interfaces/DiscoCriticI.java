package safe_rl.agent_interfaces;

public interface DiscoCriticI<V> {
    void fitCritic(double error);
    double readCritic();
}
