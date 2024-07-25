package domain_design_tabular_q_learning.domain.environment.value_objects;

public interface ActionI<A> {
    A getProperties();
    int ordinal();
}
