package maze_domain_design.services;

import lombok.Builder;
import lombok.Getter;
import maze_domain_design.domain.agent.Agent;
import maze_domain_design.environments.obstacle_on_road.EnvironmentRoad;
import maze_domain_design.domain.trainer.Trainer;

@Builder
@Getter
public class TrainingService {
    EnvironmentRoad environment;
    Trainer trainer;
    Agent agent;

   public void train() {
       trainer.train();
   }
}
