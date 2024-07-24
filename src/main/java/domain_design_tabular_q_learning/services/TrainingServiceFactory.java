package domain_design_tabular_q_learning.services;

import domain_design_tabular_q_learning.domain.agent.Agent;
import domain_design_tabular_q_learning.domain.agent.value_objects.AgentProperties;
import domain_design_tabular_q_learning.environments.obstacle_on_road.EnvironmentRoad;
import domain_design_tabular_q_learning.domain.trainer.Trainer;
import domain_design_tabular_q_learning.domain.trainer.value_objects.TrainerProperties;

public class TrainingServiceFactory {

    public static TrainingService createRoadMaze() {
        EnvironmentRoad environment = EnvironmentRoad.roadMaze();

        var trainerProperties = TrainerProperties.roadObstacle();
        var agent = new Agent(AgentProperties.roadMaze(), environment);
        var trainer = new Trainer(environment, agent, trainerProperties);
        return TrainingService.builder()
                .environment(environment)
                .trainer(trainer)
                .agent(agent)
                .build();

    }
}
