package domain_design_tabular_q_learning.environments.tunnels;

import common.math.NormalSampler;
import domain_design_tabular_q_learning.domain.environment.EnvironmentI;
import domain_design_tabular_q_learning.domain.environment.value_objects.ActionI;
import domain_design_tabular_q_learning.domain.environment.value_objects.StateI;
import domain_design_tabular_q_learning.domain.environment.value_objects.StepReturn;
import domain_design_tabular_q_learning.environments.avoid_obstacle.*;
import domain_design_tabular_q_learning.environments.shared.GridVariables;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.RandomUtils;

import java.util.function.BiFunction;

/**
 * Not optimal with the casting in roadMazeI(), method needed to comply with TrainingService:createRoadMaze()
 */

@AllArgsConstructor
public class EnvironmentTunnels implements EnvironmentI<GridVariables, TunnelActionProperties,PropertiesTunnels> {
    @Override
    public StepReturn<GridVariables> step(StateI<GridVariables> s, ActionI<TunnelActionProperties> a) {
        return null;
    }

    @Override
    public StateI<GridVariables> getStartState() {
        return null;
    }

    @Override
    public PropertiesTunnels getProperties() {
        return null;
    }

    @Override
    public void setProperties(PropertiesTunnels propertiesTunnels) {

    }

    @Override
    public ActionI<TunnelActionProperties>[] actions() {
        return new ActionI[0];
    }

    @Override
    public ActionI<TunnelActionProperties> randomAction() {
        return null;
    }

  /* @Getter @Setter
   PropertiesTunnels properties;

    public static EnvironmentTunnels tunnels() {
        return new EnvironmentTunnels(PropertiesTunnels.newDefault());
    }

    public static <V, A> EnvironmentI<V, A> tunnelsI() {
        return (EnvironmentI<V, A>) new EnvironmentTunnels(PropertiesTunnels.newDefault());
    }

    @Override
    public StepReturn<GridVariables> step(StateI<GridVariables> s, ActionI<TunnelActionProperties> a) {
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
    public StateI<GridVariables> getStartState() {
        var xMinMax=properties.startXMinMax();
        var yMinMax=properties.startYMinMax();
        return  StateTunnels.of(
                RandomUtils.nextInt(xMinMax.getFirst(),xMinMax.getSecond()+1),
                RandomUtils.nextInt(yMinMax.getFirst(),yMinMax.getSecond()+1),
                properties);
    }

    @Override
    public ActionTunnel[] actions() {
        return ActionTunnel.values();
    }

    public ActionTunnel randomAction() {
        int randIdx= RandomUtils.nextInt(0, ActionTunnel.values().length);
        return ActionTunnel.values()[randIdx];
    }

    public boolean isMove(ActionI<TunnelActionProperties> a) {
        return a.equals(ActionTunnel.N) || a.equals(ActionTunnel.S);
    }

    static BiFunction<Boolean, Double, Double> valueIfTrue = (c, v) -> c ? v : 0d;

    double getReward(boolean isTerminal, boolean isFail, boolean isMove) {
        return valueIfTrue.apply(isTerminal, properties.rewardNonFailTerminal()) +
                valueIfTrue.apply(isFail, getFailReward()) +
                valueIfTrue.apply(isMove, properties.rewardMove());
    }

    private double getFailReward() {
        var sampler= new NormalSampler(properties.rewardFailTerminalExp(), properties.rewardFailTerminalStd());
        return sampler.generateSample();
    }


    StateI<GridVariables> getNextState(StateI<GridVariables> s, ActionI<TunnelActionProperties> a) {
        var xNext = s.getVariables().x() + 1;
        var yNext = s.getVariables().y() + a.getProperties().deltaY();
        return StateRoad.of(xNext, yNext,properties).clip();
    }*/
}
