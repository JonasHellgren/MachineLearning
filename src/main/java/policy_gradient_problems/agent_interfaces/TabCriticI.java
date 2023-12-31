package policy_gradient_problems.agent_interfaces;

public interface TabCriticI<V> {
    void changeCritic(int key, double change);
    double getCriticValue(int key);
}
