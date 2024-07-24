package domain_design_tabular_q_learning.domain.trainer;

import lombok.Getter;
import domain_design_tabular_q_learning.domain.agent.Agent;
import domain_design_tabular_q_learning.domain.environment.EnvironmentI;
import domain_design_tabular_q_learning.domain.trainer.aggregates.Mediator;
import domain_design_tabular_q_learning.domain.trainer.value_objects.TrainerProperties;

public class Trainer<V> {
   @Getter
   Mediator<V> mediator;

    public Trainer(EnvironmentI<V> environment,
                   Agent<V> agent,
                   TrainerProperties properties) {
        this.mediator= new Mediator<>(environment, agent, properties);
    }

    public void train() {
    mediator.train();
}

}
