package domain_design_tabular_q_learning.environments.tunnels;

import domain_design_tabular_q_learning.domain.environment.EnvironmentI;
import domain_design_tabular_q_learning.domain.environment.value_objects.ActionI;
import domain_design_tabular_q_learning.domain.environment.value_objects.StateI;
import domain_design_tabular_q_learning.domain.environment.value_objects.StepReturn;
import domain_design_tabular_q_learning.environments.shared.XyPos;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.RandomUtils;

/**
 *       0  1  2  3  4  5  6  7  8
 * 3   |##|##|##|##|  | F|##|##|##|                  F <=> reward = -10
 * 2   |##| F|  |  |  |##|##|##|##|
 * 1   |  |  |  |##|  |  |  |  |20|
 * 0   |##| F|  |10|##|##|##|##|##|

 * Not optimal with the casting in tunnelsI(), potential future refactoring to avoid
 */

@AllArgsConstructor
public class EnvironmentTunnels implements EnvironmentI<XyPos, TunnelActionProperties, PropertiesTunnels> {

    @Getter
    @Setter
    PropertiesTunnels properties;

    public static EnvironmentTunnels newDefault() {
        return new EnvironmentTunnels(PropertiesTunnels.newDefault());
    }

    public static <V, A, P> EnvironmentI<V, A, P> newDefaultI() {
        return (EnvironmentI<V, A, P>) new EnvironmentTunnels(PropertiesTunnels.newDefault());
    }

    @Override
    public StepReturn<XyPos> step(StateI<XyPos> s, ActionI<TunnelActionProperties> a) {
        var sNext = getNextState(s, a);
        var isTerminal = sNext.isTerminal();
        var isTermFail = properties.isTermFail(sNext.getVariables());
        var isTermNonFail = properties.isTerminalNonFail(sNext.getVariables());
        var reward = getReward(sNext.getVariables(), isTermNonFail, isTermFail);
        return StepReturn.<XyPos>builder()
                .sNext(sNext).reward(reward)
                .isFail(isTermFail).isTerminal(isTerminal)
                .build();
    }

    @Override
    public StateI<XyPos> getStartState() {
        XyPos randPos = properties.getRandStartPos();
        return StateTunnels.ofXy(randPos.x(), randPos.y(), properties);
    }


    @Override
    public ActionTunnel[] actions() {
        return ActionTunnel.values();
    }

    public ActionTunnel randomAction() {
        int randIdx = RandomUtils.nextInt(0, ActionTunnel.values().length);
        return ActionTunnel.values()[randIdx];
    }


    double getReward(XyPos pos, boolean isTermNonFail, boolean isTermFail) {
        Double termNonFailValue = isTermNonFail?properties.rewardOfTerminalNonFail(pos).orElseThrow():0;
        Double termFailValue = isTermFail?properties.rewardOfFail(pos).orElseThrow():0;
        return valueIfTrue.apply(isTermNonFail, termNonFailValue) +
                properties.rewardMove() +
                valueIfTrue.apply(isTermFail, termFailValue);
    }

    /**
     * clip() <=> keep inside map
     */

    StateI<XyPos> getNextState(StateI<XyPos> s, ActionI<TunnelActionProperties> a) {
        XyPos pos = s.getVariables();
        XyPos newPos = getNewPosKeepOldIfEntersBlockedPos(a, pos);
        return StateTunnels.ofXy(newPos.x(), newPos.y(), properties).clip();
    }

    XyPos getNewPosKeepOldIfEntersBlockedPos(ActionI<TunnelActionProperties> a, XyPos pos) {
        TunnelActionProperties p = a.getProperties();
        XyPos newPos0 = XyPos.of(pos.x() + p.deltaX(), pos.y() + p.deltaY());
        return properties.blockedPositions().contains(newPos0)
                ? pos
                : newPos0;
    }
}
