package domain_design_tabular_q_learning.environments.tunnels;

import common.other.RandUtils;
import domain_design_tabular_q_learning.domain.environment.EnvironmentI;
import domain_design_tabular_q_learning.domain.environment.value_objects.ActionI;
import domain_design_tabular_q_learning.domain.environment.value_objects.StateI;
import domain_design_tabular_q_learning.domain.environment.value_objects.StepReturn;
import domain_design_tabular_q_learning.environments.shared.XyPos;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.RandomUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Not optimal with the casting in tunnelsI(), potential future refactoring to avoid
 */

@AllArgsConstructor
public class EnvironmentTunnels implements EnvironmentI<XyPos,TunnelActionProperties,PropertiesTunnels> {

   @Getter @Setter
   PropertiesTunnels properties;

    public static EnvironmentTunnels tunnels() {
        return new EnvironmentTunnels(PropertiesTunnels.newDefault());
    }

    public static <V, A, P> EnvironmentI<V, A, P> tunnelsI() {
        return (EnvironmentI<V, A,P>) new EnvironmentTunnels(PropertiesTunnels.newDefault());
    }

    @Override
    public StepReturn<XyPos> step(StateI<XyPos> s, ActionI<TunnelActionProperties> a) {
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
        List<XyPos> startList=new ArrayList<>(properties.startPositions());
        int randIdx= RandUtils.getRandomIntNumber(0,startList.size());
        XyPos randPos = startList.get(randIdx);
        return  StateTunnels.of(randPos.x(),randPos.y(),properties);
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
        return valueIfTrue.apply(isTerminal, 0d) +
                valueIfTrue.apply(isFail, 0d) +
                valueIfTrue.apply(isMove, properties.rewardMove());
    }



    StateI<XyPos> getNextState(StateI<XyPos> s, ActionI<TunnelActionProperties> a) {
        var xNext = s.getVariables().x() + 1;
        var yNext = s.getVariables().y() + a.getProperties().deltaY();
        return StateTunnels.of(xNext, yNext,properties).clip();
    }
}
