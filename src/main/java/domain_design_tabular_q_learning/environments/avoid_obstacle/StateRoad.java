package domain_design_tabular_q_learning.environments.avoid_obstacle;

import common.math.MathUtils;
import domain_design_tabular_q_learning.domain.environment.value_objects.StateI;
import domain_design_tabular_q_learning.environments.shared.XyPos;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.math3.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record StateRoad  (
                               XyPos variables,
                               PropertiesRoad properties
) implements StateI<XyPos> {

    public static  StateI<XyPos> ofXy(Integer x, Integer y, PropertiesRoad p) {
        return new StateRoad(XyPos.of(x,y),p);
    }

    public static  StateI<XyPos> ofPos(XyPos pos, PropertiesRoad p) {
        return new StateRoad(pos,p);
    }

    public static  StateI<XyPos> ofRandom(PropertiesRoad p) {
        return new StateRoad(XyPos.of(0,0),p).random();
    }

    @Override
    public XyPos getVariables() {
        return variables;
    }

    @Override
    public StateI<XyPos> newWithVariables(XyPos xyPos) {
        return new StateRoad(xyPos,properties);
    }

    @Override
    public StateI<XyPos> random() {
        return StateRoad.ofXy(
                getRand(properties.minMaxX()),
                getRand(properties.minMaxY()),
                properties);
    }


    @Override
    public StateI<XyPos> clip() {
        return StateRoad.ofPos(
                variables.posClipped(properties.minMaxX(), properties.minMaxY()),
                properties);
    }


    @Override
    public boolean isTerminal() {
        return Objects.equals(variables.x(), properties.xTerminal());
    }

    @Override
    public boolean isFail() {
        return Objects.equals(variables.y(), properties.yFailTerminal()) &&
                isTerminal();
    }


    private static int getRand(Pair<Integer, Integer> pair) {
        return RandomUtils.nextInt(pair.getFirst(), pair.getSecond()+1);
    }

    @Override
    public String toString() {
        return "(x,y)=("+variables.x()+","+variables.y()+")";
    }
}
