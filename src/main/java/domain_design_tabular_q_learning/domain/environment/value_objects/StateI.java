package domain_design_tabular_q_learning.domain.environment.value_objects;

public interface StateI<V> {
    V getVariables();
    StateI<V> newWithVariables(V v);
    boolean isTerminal();
    boolean isFail();
    StateI<V> random();
    StateI<V> clip();
}
