package domain_design_tabular_q_learning.services;

import domain_design_tabular_q_learning.domain.agent.Agent;
import domain_design_tabular_q_learning.domain.agent.value_objects.AgentProperties;
import domain_design_tabular_q_learning.domain.environment.EnvironmentI;
import domain_design_tabular_q_learning.environments.avoid_obstacle.EnvironmentRoad;
import domain_design_tabular_q_learning.domain.trainer.Trainer;
import domain_design_tabular_q_learning.domain.trainer.value_objects.TrainerProperties;
import domain_design_tabular_q_learning.environments.tunnels.EnvironmentTunnels;

public class TrainingServiceFactory {

    private TrainingServiceFactory() {
    }

    public static <V,A,P> TrainingService<V,A,P> createRoadMaze() {
        EnvironmentI<V,A,P> environment =
                 EnvironmentRoad.roadMazeI();
        var trainerProperties = TrainerProperties.roadObstacle();
        var agent = new Agent<>(AgentProperties.roadMaze(), environment);
        var trainer = new Trainer<>(environment, agent, trainerProperties);
        return TrainingService.<V,A,P>builder()
                .environment(environment)
                .trainer(trainer)
                .agent(agent)
                .build();
    }

    public static <V,A,P> TrainingService<V,A,P> createTunnels() {
        EnvironmentI<V,A,P> environment =
                EnvironmentTunnels.newDefaultI();
        var trainerProperties = TrainerProperties.tunnels();
        var agent = new Agent<>(AgentProperties.tunnels(), environment);
        var trainer = new Trainer<>(environment, agent, trainerProperties);
        return TrainingService.<V,A,P>builder()
                .environment(environment)
                .trainer(trainer)
                .agent(agent)
                .build();
    }


}
