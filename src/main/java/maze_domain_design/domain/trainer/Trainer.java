package maze_domain_design.domain.trainer;

import lombok.Getter;
import maze_domain_design.domain.agent.Agent;
import maze_domain_design.domain.environment.EnvironmentI;
import maze_domain_design.domain.trainer.aggregates.Mediator;
import maze_domain_design.domain.trainer.value_objects.TrainerProperties;

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
