package domain_design_tabular_q_learning.environments.obstacle_on_road;

import domain_design_tabular_q_learning.domain.environment.value_objects.ActionI;
import lombok.AllArgsConstructor;
import lombok.Getter;
import domain_design_tabular_q_learning.domain.environment.EnvironmentI;
import domain_design_tabular_q_learning.domain.environment.value_objects.StateI;
import domain_design_tabular_q_learning.domain.environment.value_objects.StepReturn;
import org.apache.commons.lang3.RandomUtils;
import java.util.function.BiFunction;

@AllArgsConstructor
public class EnvironmentRoad implements EnvironmentI<GridVariables,GridActionProperties> {

   @Getter
   PropertiesRoad properties;

    public static EnvironmentRoad roadMaze() {
        return new EnvironmentRoad(PropertiesRoad.roadMaze());
    }

    @Override
    public StepReturn<GridVariables> step(StateI<GridVariables> s, ActionI<GridActionProperties> a) {
        var sNext = getNextState(s, a);
        var isTerminal = sNext.isTerminal();
        var isFail = sNext.isFail();
        var isMove = isMove(a);
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

    @Override
//    public ActionI<GridActionProperties>[] actions() {
    public ActionRoad[] actions() {
        return ActionRoad.values();
    }

    public  ActionRoad randomAction() {
        int randIdx= RandomUtils.nextInt(0, ActionRoad.values().length);
        return ActionRoad.values()[randIdx];
    }

    public boolean isMove(ActionI<GridActionProperties> a) {
        return a.equals(ActionRoad.N) || a.equals(ActionRoad.S);
    }

    static BiFunction<Boolean, Double, Double> valueIfTrue = (c, v) -> c ? v : 0d;

    double getReward(boolean isTerminal, boolean isFail, boolean isMove) {
        return valueIfTrue.apply(isTerminal, properties.rewardNonFailTerminal()) +
                valueIfTrue.apply(isFail, properties.rewardFailTerminal()) +
                valueIfTrue.apply(isMove, properties.rewardMove());
    }

    StateRoad getNextState(StateI<GridVariables> s, ActionI<GridActionProperties> a) {
        var xNext = s.getVariables().x() + 1;
        var yNext = s.getVariables().y() + a.getProperties().deltaY();
        return StateRoad.of(xNext, yNext,properties).clip();
    }
}
