package maze_domain_design;

import maze_domain_design.domain.agent.Agent;
import maze_domain_design.domain.agent.value_objects.AgentProperties;
import maze_domain_design.domain.agent.value_objects.StateAction;
import maze_domain_design.environments.obstacle_on_road.EnvironmentRoad;
import maze_domain_design.domain.environment.value_objects.Action;
import maze_domain_design.environments.obstacle_on_road.GridVariables;
import maze_domain_design.environments.obstacle_on_road.StateRoad;
import maze_domain_design.domain.trainer.Trainer;
import maze_domain_design.domain.trainer.aggregates.Mediator;
import maze_domain_design.domain.trainer.value_objects.TrainerProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestTrainerRoad {

    public static final int PROB_RANDOM_ZERO = 0;
    Trainer<GridVariables> trainer;
    EnvironmentRoad environment;
    Agent<GridVariables> agent;
    TrainerProperties trainerProperties;

    @BeforeEach
    void init() {
        trainerProperties = TrainerProperties.roadMaze();
        environment = EnvironmentRoad.roadMaze();
        agent = new Agent<>(AgentProperties.roadMaze(), environment);
        trainer = new Trainer<>(environment, agent, trainerProperties);
    }

    @Test
    void whenRunningEpisode_thenEpisodeCreated() {
        var epis = trainer.getMediator().runEpisode();
        Assertions.assertTrue(epis.size() > 0);
    }

    @Test
    void whenTraining_thenCorrect() {
        trainer.train();
        Mediator<GridVariables> mediator = trainer.getMediator();

        System.out.println("agent.getMemory() = " + agent.getMemory());
        var val21Same = getVal21(Action.E);
        var val21Down = getVal21(Action.S);
        var bestAction21 = agent.chooseAction(getState(2, 1), PROB_RANDOM_ZERO);
        var bestAction00 = agent.chooseAction(getState(0, 0), PROB_RANDOM_ZERO);
        Assertions.assertTrue(mediator.getRecorder().size() > PROB_RANDOM_ZERO);
        Assertions.assertTrue(val21Same < val21Down);
        Assertions.assertEquals(Action.S, bestAction21);
        Assertions.assertEquals(Action.E, bestAction00);
    }

    Double getVal21(Action a) {
        return agent.getMemory().read(StateAction.of(getState(2, 1), a));
    }

    StateRoad getState(int x, int y) {
        var ep = environment.getProperties();
        return StateRoad.of(x, y, ep);
    }


}
