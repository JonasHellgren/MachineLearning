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
            "0,1,E, 1,1",  //x,y,action, xDes,yDes
            "0,1,N, 0,1",
            "0,1,S, 0,1",
            "0,1,W, 0,1", //state clip <=> same pos (can't go outside map)
            "1,1,E, 2,1",
            "1,1,N, 1,2",
            "1,1,W, 0,1",
            "1,1,N, 1,2",
            "2,1,E, 2,1",
            "3,2,E, 4,2",
            "4,2,N, 4,3",
            "4,3,N, 4,3",  //state clip
            "7,1,E, 8,1",
    })
    void whenStep_thenCorrectStateNext(ArgumentsAccessor arguments) {
        var s = getState(arguments);
        var a = getAction(arguments);
        var sDes = getStateDes(arguments);
        var sr = environment.step(s, a);
        assertEquals(sDes, sr.sNext());
    }

    @ParameterizedTest
    @CsvSource({
            "0,1,E, -1",  //x,y,action, rewardDes
            "0,1,W, -1",
            "0,1,N, -1",
            "0,1,S, -1",
            "1,1,N, -11",
            "2,0,E, 8",
            "2,0,S, -1",
            "4,3,E, -11",
            "7,1,E, 9"})
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
