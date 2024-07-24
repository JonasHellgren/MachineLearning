package maze_domain_design.domain.trainer;

import lombok.Getter;
import maze_domain_design.domain.agent.Agent;
import maze_domain_design.environments.obstacle_on_road.EnvironmentRoad;
import maze_domain_design.domain.trainer.aggregates.Mediator;
import maze_domain_design.domain.trainer.value_objects.TrainerProperties;

public class Trainer {
   @Getter
   Mediator mediator;

    public Trainer(EnvironmentRoad environment,
                   Agent agent,
                   TrainerProperties properties) {
        this.mediator=new Mediator(environment,agent,properties);
    }

    public void train() {
    mediator.train();
}

}
