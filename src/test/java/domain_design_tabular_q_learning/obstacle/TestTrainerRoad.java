package domain_design_tabular_q_learning.obstacle;

import domain_design_tabular_q_learning.domain.agent.Agent;
import domain_design_tabular_q_learning.domain.agent.value_objects.AgentProperties;
import domain_design_tabular_q_learning.domain.agent.value_objects.StateAction;
import domain_design_tabular_q_learning.domain.environment.value_objects.StateI;
import domain_design_tabular_q_learning.environments.avoid_obstacle.*;
import domain_design_tabular_q_learning.domain.trainer.Trainer;
import domain_design_tabular_q_learning.domain.trainer.aggregates.Mediator;
import domain_design_tabular_q_learning.domain.trainer.value_objects.TrainerProperties;
import domain_design_tabular_q_learning.environments.shared.XyPos;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestTrainerRoad {

    public static final int PROB_RANDOM_ZERO = 0;
    Trainer<XyPos, RoadActionProperties,PropertiesRoad> trainer;
    EnvironmentRoad environment;
    Agent<XyPos, RoadActionProperties,PropertiesRoad> agent;
    TrainerProperties trainerProperties;

    @BeforeEach
    void init() {
        trainerProperties = TrainerProperties.roadObstacle();
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
        Mediator<XyPos, RoadActionProperties,PropertiesRoad> mediator = trainer.getMediator();

        System.out.println("agent.getMemory() = " + agent.getMemory());
        var val21Same = getVal21(ActionRoad.E);
        var val21Down = getVal21(ActionRoad.S);
        var bestAction21 = agent.chooseAction(getState(2, 1), PROB_RANDOM_ZERO);
        var bestAction00 = agent.chooseAction(getState(0, 0), PROB_RANDOM_ZERO);
        Assertions.assertTrue(mediator.getRecorder().size() > PROB_RANDOM_ZERO);
        Assertions.assertTrue(val21Same < val21Down);
        Assertions.assertEquals(ActionRoad.S, bestAction21);
        Assertions.assertEquals(ActionRoad.E, bestAction00);
    }

    Double getVal21(ActionRoad a) {
        return agent.getMemory().read(StateAction.of(getState(2, 1), a));
    }

    StateI<XyPos> getState(int x, int y) {
        var ep = environment.getProperties();
        return StateRoad.ofXy(x, y, ep);
    }


}
