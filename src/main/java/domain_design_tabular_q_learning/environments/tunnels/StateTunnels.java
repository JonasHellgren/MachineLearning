package domain_design_tabular_q_learning.environments.tunnels;

import domain_design_tabular_q_learning.domain.environment.value_objects.StateI;
import domain_design_tabular_q_learning.environments.shared.XyPos;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.math3.util.Pair;

public record StateTunnels(
                               XyPos variables,
                               PropertiesTunnels properties
) implements StateI<XyPos> {

    public static  StateI<XyPos> ofXy(Integer x, Integer y, PropertiesTunnels p) {
        return new StateTunnels(XyPos.of(x,y),p);
    }

    public static  StateI<XyPos> ofPos(XyPos pos, PropertiesTunnels p) {
        return new StateTunnels(pos,p);
    }

    public static  StateI<XyPos> ofRandom(PropertiesTunnels p) {
        return new StateTunnels(XyPos.of(0,0),p).random();
    }

    @Override
    public XyPos getVariables() {
        return variables;
    }

    @Override
    public StateI<XyPos> newWithVariables(XyPos xyPos) {
        return new StateTunnels(xyPos,properties);
    }

    @Override
    public StateI<XyPos> random() {
        return StateTunnels.ofXy(
                getRand(properties.minMaxX()),
                getRand(properties.minMaxY()),
                properties);
    }


    @Override
    public StateI<XyPos> clip() {
        return StateTunnels.ofPos(
                variables.posClipped(properties.minMaxX(), properties.minMaxY()),
                properties);
    }

    @Override
    public boolean isTerminal() {
        return properties().isTerminalNonFail(variables) || isFail();
    }

    @Override
    public boolean isFail() {
        return properties().isTermFail(variables);
    }


    private static int getRand(Pair<Integer, Integer> pair) {
        return RandomUtils.nextInt(pair.getFirst(), pair.getSecond()+1);
    }

    @Override
    public String toString() {
        return "(x,y)=("+variables.x()+","+variables.y()+")";
    }
}
