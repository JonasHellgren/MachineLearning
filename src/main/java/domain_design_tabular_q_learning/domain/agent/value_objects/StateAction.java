package domain_design_tabular_q_learning.domain.agent.value_objects;

import domain_design_tabular_q_learning.domain.environment.value_objects.ActionI;
import domain_design_tabular_q_learning.domain.environment.value_objects.StateI;
import domain_design_tabular_q_learning.domain.trainer.entities.Experience;

public record StateAction<V,A>(StateI<V> state, ActionI<A> action) {
    public static <V,A> StateAction<V,A> of(StateI<V> s, ActionI<A> a){
        return new StateAction<>(s,a);
    }

    public static <V,A> StateAction<V,A> ofSars(Experience<V,A> e){
        return new StateAction<>(e.getSars().s(), e.getSars().a());
    }

}