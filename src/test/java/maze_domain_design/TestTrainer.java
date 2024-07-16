package maze_domain_design;

import maze_domain_design.domain.agent.Agent;
import maze_domain_design.domain.agent.value_objects.AgentProperties;
import maze_domain_design.domain.environment.Environment;
import maze_domain_design.domain.trainer.Trainer;
import maze_domain_design.domain.trainer.aggregates.Mediator;
import maze_domain_design.domain.trainer.value_objects.TrainerProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestTrainer {

    Trainer trainer;
    Environment environment;
    Agent agent;
    TrainerProperties trainerProperties;

    @BeforeEach
    void init() {
        trainerProperties=TrainerProperties.roadMaze().withNEpisodes(1_000);
        environment = Environment.roadMaze();
        agent=new Agent(AgentProperties.roadMaze(), environment);
        trainer=new Trainer(environment,agent,trainerProperties);
    }

    @Test
    void whenRunningEpisode_thenEpisodeCreated() {
        var epis=trainer.getMediator().runEpisode();
        Assertions.assertTrue(epis.size()>0);
    }

    @Test
    void whenTraining_thenCorrect() {
        trainer.train();
        Mediator mediator = trainer.getMediator();

        System.out.println("agent.getMemory() = " + agent.getMemory());
        //  System.out.println("mediator.getRecorder() = " + mediator.getRecorder());
        Assertions.assertTrue(mediator.getRecorder().size()>0);
    }


}
