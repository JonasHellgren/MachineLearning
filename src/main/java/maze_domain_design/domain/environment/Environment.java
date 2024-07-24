package maze_domain_design.domain.environment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import maze_domain_design.domain.environment.value_objects.Action;
import maze_domain_design.domain.environment.value_objects.PropertiesRoadMaze;
import maze_domain_design.environments.obstacle_on_road.StateRoad;
import maze_domain_design.domain.environment.value_objects.StepReturn;
import java.util.function.BiFunction;

@AllArgsConstructor
public class Environment {  //todo flytta till folder environments

   @Getter
   PropertiesRoadMaze properties;

    public static Environment roadMaze() {
        return new Environment(PropertiesRoadMaze.roadMaze());
    }

    public StepReturn step(StateRoad s, Action a) {
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

    StateRoad getNextState(StateRoad s, Action a) {
        var xNext = s.getVariables().x() + 1;
        var yNext = s.getVariables().y() + a.deltaY;
        return StateRoad.of(xNext, yNext,properties).clip();
    }
}
