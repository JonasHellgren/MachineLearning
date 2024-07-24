package maze_domain_design.services;

import maze_domain_design.domain.agent.Agent;
import maze_domain_design.domain.agent.value_objects.AgentProperties;
import maze_domain_design.environments.obstacle_on_road.EnvironmentRoad;
import maze_domain_design.domain.trainer.Trainer;
import maze_domain_design.domain.trainer.value_objects.TrainerProperties;

public class TrainingServiceFactory {

    public static TrainingService createRoadMaze() {
        EnvironmentRoad environment = EnvironmentRoad.roadMaze();

        var trainerProperties = TrainerProperties.roadMaze();
        var agent = new Agent(AgentProperties.roadMaze(), environment);
        var trainer = new Trainer(environment, agent, trainerProperties);
        return TrainingService.builder()
                .environment(environment)
                .trainer(trainer)
                .agent(agent)
                .build();

    }
}
