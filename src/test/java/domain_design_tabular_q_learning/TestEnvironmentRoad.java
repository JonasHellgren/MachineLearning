package domain_design_tabular_q_learning;

import domain_design_tabular_q_learning.domain.environment.value_objects.StateI;
import domain_design_tabular_q_learning.environments.avoid_obstacle.EnvironmentRoad;
import domain_design_tabular_q_learning.environments.avoid_obstacle.ActionRoad;
import domain_design_tabular_q_learning.environments.avoid_obstacle.GridVariables;
import domain_design_tabular_q_learning.environments.avoid_obstacle.StateRoad;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TestEnvironmentRoad {

    EnvironmentRoad environment;

    @BeforeEach
    void init() {
        environment = EnvironmentRoad.roadMaze();
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

    StateI<GridVariables> getStateDes(ArgumentsAccessor arguments) {
        int xDes = arguments.getInteger(3);
        int yDes = arguments.getInteger(4);
        return StateRoad.of(xDes, yDes, environment.getProperties());
    }

    ActionRoad getAction(ArgumentsAccessor arguments) {
        String aName = arguments.getString(2);
        return ActionRoad.valueOf(aName);
    }

    StateI<GridVariables> getState(ArgumentsAccessor arguments) {
        int x = arguments.getInteger(0);
        int y = arguments.getInteger(1);
        return StateRoad.of(x, y, environment.getProperties());
    }

    double getRewardDes(ArgumentsAccessor arguments) {
        return arguments.getDouble(3);
    }
}
