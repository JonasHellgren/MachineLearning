package domain_design_tabular_q_learning.domain.environment.value_objects;

public interface StateI<V> {
    V getVariables();
    boolean isTerminal();
    boolean isFail();
    StateI<V> random();
    StateI<V> clip();
}
