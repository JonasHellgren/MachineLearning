package domain_design_tabular_q_learning.services;

import domain_design_tabular_q_learning.domain.environment.EnvironmentI;
import lombok.Builder;
import lombok.Getter;
import domain_design_tabular_q_learning.domain.agent.Agent;
import domain_design_tabular_q_learning.domain.trainer.Trainer;

@Builder
@Getter
public class TrainingService<V,A,P> {
    EnvironmentI<V,A,P> environment;
    Trainer<V,A,P> trainer;
    Agent<V,A,P> agent;

   public void train() {
       trainer.train();
   }
}
