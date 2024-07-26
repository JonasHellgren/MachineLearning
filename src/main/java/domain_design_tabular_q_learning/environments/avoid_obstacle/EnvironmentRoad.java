package domain_design_tabular_q_learning.environments.avoid_obstacle;

import common.math.NormalSampler;
import domain_design_tabular_q_learning.domain.environment.value_objects.ActionI;
import domain_design_tabular_q_learning.environments.shared.XyPos;
import lombok.AllArgsConstructor;
import lombok.Getter;
import domain_design_tabular_q_learning.domain.environment.EnvironmentI;
import domain_design_tabular_q_learning.domain.environment.value_objects.StateI;
import domain_design_tabular_q_learning.domain.environment.value_objects.StepReturn;
import lombok.Setter;
import org.apache.commons.lang3.RandomUtils;
import java.util.function.BiFunction;

/**
 * Not optimal with the casting in roadMazeI(), method needed to comply with TrainingService:createRoadMaze()
 */

@AllArgsConstructor
public class EnvironmentRoad implements EnvironmentI<XyPos, RoadActionProperties,PropertiesRoad> {

   @Getter @Setter
   PropertiesRoad properties;

    public static  EnvironmentRoad roadMaze() {
        return new EnvironmentRoad(PropertiesRoad.roadMaze());
    }

    public static <V, A,P> EnvironmentI<V, A, P> roadMazeI() {
        return (EnvironmentI<V, A,P>) new EnvironmentRoad(PropertiesRoad.roadMaze());
    }

    @Override
    public StepReturn<XyPos> step(StateI<XyPos> s, ActionI<RoadActionProperties> a) {
        var sNext = getNextState(s, a);
        var isTerminal = sNext.isTerminal();
        var isFail = sNext.isFail();
        var isMove = isMove(a);
        var reward = getReward(isTerminal, isFail, isMove);
        return StepReturn.<XyPos>builder()
                .sNext(sNext).reward(reward)
                .isFail(isFail).isTerminal(isTerminal)
                .build();
    }

    @Override
    public StateI<XyPos> getStartState() {
        var xMinMax=properties.startXMinMax();
        var yMinMax=properties.startYMinMax();
        return  StateRoad.of(
                RandomUtils.nextInt(xMinMax.getFirst(),xMinMax.getSecond()+1),
                RandomUtils.nextInt(yMinMax.getFirst(),yMinMax.getSecond()+1),
                properties);
    }

    @Override
    public ActionRoad[] actions() {
        return ActionRoad.values();
    }

    public  ActionRoad randomAction() {
        int randIdx= RandomUtils.nextInt(0, ActionRoad.values().length);
        return ActionRoad.values()[randIdx];
    }

    public boolean isMove(ActionI<RoadActionProperties> a) {
        return a.equals(ActionRoad.N) || a.equals(ActionRoad.S);
    }

    double getReward(boolean isTerminal, boolean isFail, boolean isMove) {
        return valueIfTrue.apply(isTerminal, properties.rewardNonFailTerminal()) +
                valueIfTrue.apply(isFail, getFailReward()) +
                valueIfTrue.apply(isMove, properties.rewardMove());
    }

    private double getFailReward() {
        var sampler= new NormalSampler(properties.rewardFailTerminalExp(), properties.rewardFailTerminalStd());
        return sampler.generateSample();
    }


    StateI<XyPos> getNextState(StateI<XyPos> s, ActionI<RoadActionProperties> a) {
        var xNext = s.getVariables().x() + 1;
        var yNext = s.getVariables().y() + a.getProperties().deltaY();
        return StateRoad.of(xNext, yNext,properties).clip();
    }
}
