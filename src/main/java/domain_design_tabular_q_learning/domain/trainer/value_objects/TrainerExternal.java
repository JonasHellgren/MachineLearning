package domain_design_tabular_q_learning.domain.trainer.value_objects;

import domain_design_tabular_q_learning.domain.agent.Agent;
import domain_design_tabular_q_learning.domain.environment.EnvironmentI;

public record TrainerExternal<V,A>(
        EnvironmentI<V,A> environment,
        Agent<V,A> agent
) {
}
