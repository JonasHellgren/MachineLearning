package maze_domain_design.domain.environment;

import common.list_arrays.ListUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import maze_domain_design.domain.environment.value_objects.Action;
import maze_domain_design.domain.environment.value_objects.EnvironmentProperties;
import maze_domain_design.domain.environment.value_objects.State;
import maze_domain_design.domain.environment.value_objects.StepReturn;
import java.util.function.BiFunction;

@AllArgsConstructor
public class Environment {

   @Getter
   EnvironmentProperties properties;

    public static Environment roadMaze() {
        return new Environment(EnvironmentProperties.roadMaze());
    }

    public StepReturn step(State s, Action a) {
        var sNext = getNextState(s, a);
        var isTerminal = sNext.isTerminal();
        var isFail = sNext.isFail(properties);
        var isMove = a.isMove();
        var reward = getReward(isTerminal, isFail, isMove);
        return StepReturn.builder()
                .sNext(sNext).reward(reward)
                .isFail(isFail).isTerminal(isTerminal)
                .build();

    }

    static BiFunction<Boolean, Double, Double> valueIfTrue = (c, v) -> c ? v : 0d;

    double getReward(boolean isTerminal, boolean isFail, boolean isMove) {
        return valueIfTrue.apply(isTerminal, properties.rewardNonFailTerminal()) +
                valueIfTrue.apply(isFail, properties.rewardFailTerminal()) +
                valueIfTrue.apply(isMove, properties.rewardMove());
    }

    State getNextState(State s, Action a) {
        var xNext = s.x() + 1;
        var yNext = s.y() + a.deltaY;
        return new State(xNext, yNext,properties).clip();
    }
}
