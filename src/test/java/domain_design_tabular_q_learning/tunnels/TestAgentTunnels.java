package domain_design_tabular_q_learning.tunnels;

import domain_design_tabular_q_learning.domain.agent.Agent;
import domain_design_tabular_q_learning.domain.agent.value_objects.AgentProperties;
import domain_design_tabular_q_learning.domain.agent.value_objects.StateAction;
import domain_design_tabular_q_learning.environments.avoid_obstacle.*;
import domain_design_tabular_q_learning.environments.shared.XyPos;
import domain_design_tabular_q_learning.environments.tunnels.*;
import org.apache.commons.math3.util.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class TestAgentTunnels {
    public static final int PROB_RANDOM_IS_ZERO = 0;
    public static final double PROB_RANDOM_IS_ONE = 1d;
    Agent<XyPos, TunnelActionProperties, PropertiesTunnels> agent;
    AgentProperties properties;
    PropertiesTunnels envProps;
    EnvironmentTunnels environment;

    @BeforeEach
    void init() {
        properties = AgentProperties.tunnels();
        envProps= PropertiesTunnels.newDefault();
        agent=new Agent<>(properties, EnvironmentTunnels.newDefault());
        environment = EnvironmentTunnels.newDefault();
    }

    @Test
    void givenNotDefinedMemory_whenStateWithXMax1_thenActionSame() {
        var s= StateTunnels.ofXy(7,1,envProps);
        var ba=agent.chooseAction(s, PROB_RANDOM_IS_ZERO);
        Assertions.assertEquals(ActionTunnel.E,ba);
    }

    @Test
    void givenNotDefinedMemory_whenState11_thenActionWorE() {
        var s= StateTunnels.ofXy(1,1,envProps);
        var ba=agent.chooseAction(s, PROB_RANDOM_IS_ZERO);
        Assertions.assertTrue(ActionTunnel.W.equals(ba) || ActionTunnel.E.equals(ba)) ;
    }

    @Test
    void givenBadValueInState43_whenState42_thenActionNotN() {
        var s43= StateTunnels.ofXy(4,3,envProps);
        Arrays.stream(environment.actions()).toList().forEach(
                a -> {
                    double badValue = -100;
                    agent.getMemory().write(StateAction.of(s43,a), badValue);
                });
        var s42= StateTunnels.ofXy(4,2,envProps);
        var ba=agent.chooseAction(s42, PROB_RANDOM_IS_ZERO);
        Assertions.assertFalse(ActionTunnel.N.equals(ba)) ;

    }

    @Test
    void givenOneProbRandomAction_whenStateRandom_thenAnyAction() {
        var s= StateTunnels.ofRandom(envProps);
        var ba=agent.chooseAction(s, PROB_RANDOM_IS_ONE);
        Assertions.assertTrue(Arrays.asList(ActionTunnel.values()).contains(ba));
    }

}
