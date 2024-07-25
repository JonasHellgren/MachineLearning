package domain_design_tabular_q_learning;

import domain_design_tabular_q_learning.domain.agent.Agent;
import domain_design_tabular_q_learning.domain.agent.value_objects.AgentProperties;
import domain_design_tabular_q_learning.domain.agent.value_objects.StateAction;
import domain_design_tabular_q_learning.environments.avoid_obstacle.*;
import domain_design_tabular_q_learning.environments.shared.GridVariables;
import org.apache.commons.math3.util.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;

public class TestAgent {
    public static final int PROB_RANDOM_IS_ZERO = 0;
    public static final double PROB_RANDOM_IS_ONE = 1d;
    Agent<GridVariables, RoadActionProperties> agent;
    AgentProperties properties;
    PropertiesRoad envProps;
    EnvironmentRoad environment;

    @BeforeEach
    void init() {
        properties = AgentProperties.roadMaze();
        envProps= PropertiesRoad.roadMaze();
        agent=new Agent<>(properties, EnvironmentRoad.roadMaze());
        environment = EnvironmentRoad.roadMaze();
    }

    @Test
    void givenNotDefinedMemory_whenStateWithXMax1_thenActionSame() {
        int maxX = 1;
        var s= StateRoad.ofRandom(envProps.withMinMaxX(Pair.create(0, maxX)));
        var ba=agent.chooseAction(s, PROB_RANDOM_IS_ZERO);
        Assertions.assertEquals(ActionRoad.E,ba);
    }

    @Test
    void givenNotDefinedMemory_whenState21_thenActionDown() {
        var s= StateRoad.of(2,1,envProps);
        var ba=agent.chooseAction(s, PROB_RANDOM_IS_ZERO);
        Assertions.assertEquals(ActionRoad.S,ba);
    }

    @Test
    void givenBadValueInState21_whenState11_thenActionDown() {
        var s21= StateRoad.of(2,1,envProps);
        Arrays.stream(environment.actions()).toList().forEach(
                a -> {
                    double badValue = -100;
                    agent.getMemory().write(StateAction.of(s21,a), badValue);
                });
        var s11= StateRoad.of(1,1,envProps);
        var ba=agent.chooseAction(s11, PROB_RANDOM_IS_ZERO);
        Assertions.assertEquals(ActionRoad.S,ba);
    }

    @Test
    void givenOneProbRandomAction_whenStateRandom_thenAnyAction() {
        var s= StateRoad.ofRandom(envProps);
        var ba=agent.chooseAction(s, PROB_RANDOM_IS_ONE);
        Assertions.assertTrue(Arrays.asList(ActionRoad.values()).contains(ba));
    }

}
