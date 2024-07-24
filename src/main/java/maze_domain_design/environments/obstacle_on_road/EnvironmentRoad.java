package maze_domain_design.environments.obstacle_on_road;

import lombok.AllArgsConstructor;
import lombok.Getter;
import maze_domain_design.domain.environment.EnvironmentI;
import maze_domain_design.domain.environment.value_objects.Action;
import maze_domain_design.domain.environment.value_objects.StateI;
import maze_domain_design.domain.environment.value_objects.StepReturn;
import org.apache.commons.lang3.RandomUtils;

import java.util.function.BiFunction;

@AllArgsConstructor
public class EnvironmentRoad implements EnvironmentI<GridVariables> {

   @Getter
   PropertiesRoad properties;

    public static EnvironmentRoad roadMaze() {
        return new EnvironmentRoad(PropertiesRoad.roadMaze());
    }

    @Override
    public StepReturn<GridVariables> step(StateI<GridVariables> s, Action a) {
        var sNext = getNextState(s, a);
        var isTerminal = sNext.isTerminal();
        var isFail = sNext.isFail();
        var isMove = a.isMove();
        var reward = getReward(isTerminal, isFail, isMove);
        return StepReturn.<GridVariables>builder()
                .sNext(sNext).reward(reward)
                .isFail(isFail).isTerminal(isTerminal)
                .build();
    }

    @Override
    public StateRoad getStartState() {
        var xMinMax=properties.startXMinMax();
        var yMinMax=properties.startYMinMax();
        return  StateRoad.of(
                RandomUtils.nextInt(xMinMax.getFirst(),xMinMax.getSecond()+1),
                RandomUtils.nextInt(yMinMax.getFirst(),yMinMax.getSecond()+1),
                properties);
    }

    static BiFunction<Boolean, Double, Double> valueIfTrue = (c, v) -> c ? v : 0d;

    double getReward(boolean isTerminal, boolean isFail, boolean isMove) {
        return valueIfTrue.apply(isTerminal, properties.rewardNonFailTerminal()) +
                valueIfTrue.apply(isFail, properties.rewardFailTerminal()) +
                valueIfTrue.apply(isMove, properties.rewardMove());
    }

    StateRoad getNextState(StateI<GridVariables> s, Action a) {
        var xNext = s.getVariables().x() + 1;
        var yNext = s.getVariables().y() + a.deltaY;
        return StateRoad.of(xNext, yNext,properties).clip();
    }
}
