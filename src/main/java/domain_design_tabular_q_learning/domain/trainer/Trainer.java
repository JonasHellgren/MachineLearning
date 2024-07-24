package domain_design_tabular_q_learning.domain.trainer;

import lombok.Getter;
import domain_design_tabular_q_learning.domain.agent.Agent;
import domain_design_tabular_q_learning.domain.environment.EnvironmentI;
import domain_design_tabular_q_learning.domain.trainer.aggregates.Mediator;
import domain_design_tabular_q_learning.domain.trainer.value_objects.TrainerProperties;

public class Trainer<V,A> {
   @Getter
   Mediator<V,A> mediator;

    public Trainer(EnvironmentI<V,A> environment,
                   Agent<V,A> agent,
                   TrainerProperties properties) {
        this.mediator= new Mediator<>(environment, agent, properties);
    }

    public void train() {
    mediator.train();
}

}
