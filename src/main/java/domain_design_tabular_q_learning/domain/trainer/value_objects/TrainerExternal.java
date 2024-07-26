package domain_design_tabular_q_learning.domain.trainer.value_objects;

import domain_design_tabular_q_learning.domain.agent.Agent;
import domain_design_tabular_q_learning.domain.environment.EnvironmentI;

public record TrainerExternal<V,A,P>(
        EnvironmentI<V,A,P> environment,
        Agent<V,A,P> agent
) {
}
