package domain_design_tabular_q_learning.tunnels;

import domain_design_tabular_q_learning.domain.environment.EnvironmentI;
import domain_design_tabular_q_learning.domain.environment.value_objects.StateI;
import domain_design_tabular_q_learning.environments.shared.XyPos;
import domain_design_tabular_q_learning.environments.tunnels.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestEnvironmentTunnels {

    EnvironmentI<
            XyPos, TunnelActionProperties, PropertiesTunnels>
            environment;

    @BeforeEach
    void init() {
        environment = EnvironmentTunnels.tunnels();
    }

    @ParameterizedTest
    @CsvSource({
            "0,0,E, 1,0",  //x,y,action, xDes,yDes
            "0,0,N, 1,1",
            "0,0,S, 1,0",
            "2,0,E, 3,0",
            "2,0,N, 3,1",
            "2,0,S, 3,0"})
    void whenStep_thenCorrectStateNext(ArgumentsAccessor arguments) {
        var s = getState(arguments);
        var a = getAction(arguments);
        var sDes = getStateDes(arguments);
        var sr = environment.step(s, a);
        assertEquals(sDes, sr.sNext());
    }

    @ParameterizedTest
    @CsvSource({
            "0,0,E, 0",  //x,y,action, rewardDes
            "0,0,N, -1",
            "0,0,S, -1",
            "2,0,E, 0",
            "2,0,N, -101",
            "2,0,S, -1"})
    void whenStep_thenCorrectReward(ArgumentsAccessor arguments) {
        var s = getState(arguments);
        var a = getAction(arguments);
        var rewardDes = getRewardDes(arguments);
        var sr = environment.step(s, a);
        assertEquals(rewardDes, sr.reward());
    }

    StateI<XyPos> getStateDes(ArgumentsAccessor arguments) {
        int xDes = arguments.getInteger(3);
        int yDes = arguments.getInteger(4);
        return StateTunnels.of(xDes, yDes, environment.getProperties());
    }

    ActionTunnel getAction(ArgumentsAccessor arguments) {
        String aName = arguments.getString(2);
        return ActionTunnel.valueOf(aName);
    }

    StateI<XyPos> getState(ArgumentsAccessor arguments) {
        int x = arguments.getInteger(0);
        int y = arguments.getInteger(1);
        return StateTunnels.of(x, y, environment.getProperties());
    }

    double getRewardDes(ArgumentsAccessor arguments) {
        return arguments.getDouble(3);
    }
}
