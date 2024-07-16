package maze_domain_design;

import maze_domain_design.domain.environment.Environment;
import maze_domain_design.domain.environment.value_objects.Action;
import maze_domain_design.domain.environment.value_objects.State;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TestEnvironment {

    Environment environment;

    @BeforeEach
    void init() {
        environment = Environment.roadMaze();
    }

    @ParameterizedTest
    @CsvSource({
            "0,0,SAME, 1,0",  //x,y,action, xDes,yDes
            "0,0,UP, 1,1",
            "0,0,DOWN, 1,0",
            "2,0,SAME, 3,0",
            "2,0,UP, 3,1",
            "2,0,DOWN, 3,0"})
    void whenStep_thenCorrectStateNext(ArgumentsAccessor arguments) {
        var s = getState(arguments);
        var a = getAction(arguments);
        var sDes = getStateDes(arguments);
        var sr = environment.step(s, a);
        assertEquals(sDes, sr.sNext());
    }

    @ParameterizedTest
    @CsvSource({
            "0,0,SAME, 0",  //x,y,action, rewardDes
            "0,0,UP, -1",
            "0,0,DOWN, -1",
            "2,0,SAME, 0",
            "2,0,UP, -101",
            "2,0,DOWN, -1"})
    void whenStep_thenCorrectReward(ArgumentsAccessor arguments) {
        var s = getState(arguments);
        var a = getAction(arguments);
        var rewardDes = getRewardDes(arguments);
        var sr = environment.step(s, a);
        assertEquals(rewardDes, sr.reward());
    }

    State getStateDes(ArgumentsAccessor arguments) {
        int xDes = arguments.getInteger(3);
        int yDes = arguments.getInteger(4);
        return State.of(xDes, yDes, environment.getProperties());
    }

    Action getAction(ArgumentsAccessor arguments) {
        String aName = arguments.getString(2);
        return Action.valueOf(aName);
    }

    State getState(ArgumentsAccessor arguments) {
        int x = arguments.getInteger(0);
        int y = arguments.getInteger(1);
        return State.of(x, y, environment.getProperties());
    }

    double getRewardDes(ArgumentsAccessor arguments) {
        return arguments.getDouble(3);
    }
}
