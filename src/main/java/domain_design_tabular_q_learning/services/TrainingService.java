package domain_design_tabular_q_learning.services;

import lombok.Builder;
import lombok.Getter;
import domain_design_tabular_q_learning.domain.agent.Agent;
import domain_design_tabular_q_learning.environments.obstacle_on_road.EnvironmentRoad;
import domain_design_tabular_q_learning.domain.trainer.Trainer;

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
