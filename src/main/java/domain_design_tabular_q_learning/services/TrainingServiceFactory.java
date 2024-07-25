package domain_design_tabular_q_learning.services;

import domain_design_tabular_q_learning.domain.agent.Agent;
import domain_design_tabular_q_learning.domain.agent.value_objects.AgentProperties;
import domain_design_tabular_q_learning.domain.environment.EnvironmentI;
import domain_design_tabular_q_learning.environments.obstacle_on_road.EnvironmentRoad;
import domain_design_tabular_q_learning.domain.trainer.Trainer;
import domain_design_tabular_q_learning.domain.trainer.value_objects.TrainerProperties;
import domain_design_tabular_q_learning.environments.obstacle_on_road.GridActionProperties;
import domain_design_tabular_q_learning.environments.obstacle_on_road.GridVariables;

public class TrainingServiceFactory {

    private TrainingServiceFactory() {
    }

    public static <V,A> TrainingService<V,A> createRoadMaze() {
        EnvironmentI<V,A> environment =
                 EnvironmentRoad.roadMazeI();
        var trainerProperties = TrainerProperties.roadObstacle();
        var agent = new Agent<>(AgentProperties.roadMaze(), environment);
        var trainer = new Trainer<>(environment, agent, trainerProperties);
        return TrainingService.<V,A>builder()
                .environment(environment)
                .trainer(trainer)
                .agent(agent)
                .build();

    }
}
