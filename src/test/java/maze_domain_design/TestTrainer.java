package maze_domain_design;

import maze_domain_design.domain.agent.Agent;
import maze_domain_design.domain.agent.value_objects.AgentProperties;
import maze_domain_design.domain.environment.Environment;
import maze_domain_design.domain.trainer.Trainer;
import maze_domain_design.domain.trainer.value_objects.TrainerProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestTrainer {

    Trainer trainer;
    Environment environment;
    Agent agent;
    TrainerProperties trainerProperties;

    @BeforeEach
    void init() {
        trainerProperties=TrainerProperties.roadMaze();
        environment = Environment.roadMaze();
        agent=new Agent(AgentProperties.roadMaze(), environment);
        trainer=new Trainer(environment,agent,trainerProperties);
    }

    @Test
    void whenRunningEpis_thenEpisCreated() {
        var epis=trainer.getMediator().createEpisode();
        System.out.println("epis.size() = " + epis.size());
        System.out.println("epis = " + epis);
    }
}
