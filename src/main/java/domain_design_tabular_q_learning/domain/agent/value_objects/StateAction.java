package domain_design_tabular_q_learning.domain.agent.value_objects;

import domain_design_tabular_q_learning.domain.environment.value_objects.Action;
import domain_design_tabular_q_learning.domain.environment.value_objects.StateI;
import domain_design_tabular_q_learning.domain.trainer.entities.Experience;

public record StateAction<V>(StateI<V> state, Action action) {
    public static <V> StateAction<V> of(StateI<V> s, Action a){
        return new StateAction<>(s,a);
    }

    public static <V> StateAction<V> ofSars(Experience<V> e){
        return new StateAction<>(e.getSars().s(), e.getSars().a());
    }

}