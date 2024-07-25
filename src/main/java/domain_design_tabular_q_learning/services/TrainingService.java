package domain_design_tabular_q_learning.services;

import domain_design_tabular_q_learning.domain.environment.EnvironmentI;
import lombok.Builder;
import lombok.Getter;
import domain_design_tabular_q_learning.domain.agent.Agent;
import domain_design_tabular_q_learning.domain.trainer.Trainer;

@Builder
@Getter
public class TrainingService<V,A> {
    EnvironmentI<V,A> environment;
    Trainer<V,A> trainer;
    Agent<V,A> agent;

   public void train() {
       trainer.train();
   }
}
