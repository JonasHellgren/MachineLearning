package domain_design_tabular_q_learning.services;

import domain_design_tabular_q_learning.domain.agent.Agent;
import domain_design_tabular_q_learning.domain.agent.value_objects.AgentProperties;
import domain_design_tabular_q_learning.domain.environment.EnvironmentI;
import domain_design_tabular_q_learning.environments.obstacle_on_road.EnvironmentRoad;
import domain_design_tabular_q_learning.domain.trainer.Trainer;
import domain_design_tabular_q_learning.domain.trainer.value_objects.TrainerProperties;

public class TrainingServiceFactory<V,A> {

    public static <V,A> TrainingService<V,A> createRoadMaze() {
        EnvironmentI<V,A> environment = (EnvironmentI<V, A>) EnvironmentRoad.roadMaze();

        var trainerProperties = TrainerProperties.roadObstacle();
        var agent = new Agent<V,A>(AgentProperties.roadMaze(), environment);
        var trainer = new Trainer<V,A>(environment, agent, trainerProperties);
        return TrainingService.<V,A>builder()
                .environment(environment)
                .trainer(trainer)
                .agent(agent)
                .build();

    }
}
