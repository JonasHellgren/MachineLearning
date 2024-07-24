package domain_design_tabular_q_learning.domain.environment.value_objects;

import java.util.List;

public interface ActionI<A> {
/*    ActionI<A> random();
    List<ActionI<A>> values();
    boolean isMove(); //todo veck*/

    A getProperties();
    int ordinal();

}
