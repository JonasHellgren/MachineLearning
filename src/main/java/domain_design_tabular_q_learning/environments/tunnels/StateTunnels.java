package domain_design_tabular_q_learning.environments.tunnels;

import common.math.MathUtils;
import domain_design_tabular_q_learning.domain.environment.value_objects.StateI;
import domain_design_tabular_q_learning.environments.shared.GridVariables;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.math3.util.Pair;

import java.util.Objects;

public record StateTunnels(
                               GridVariables variables,
                               PropertiesTunnels properties
) implements StateI<GridVariables> {

    public static  StateI<GridVariables> of(Integer x, Integer y, PropertiesTunnels p) {
        return new StateTunnels(GridVariables.of(x,y),p);
    }

    public static  StateI<GridVariables> ofRandom(PropertiesTunnels p) {
        return new StateTunnels(GridVariables.of(0,0),p).random();
    }

    @Override
    public GridVariables getVariables() {
        return variables;
    }

    @Override
    public StateI<GridVariables> newWithVariables(GridVariables gridVariables) {
        return new StateTunnels(gridVariables,properties);
    }

    @Override
    public StateI<GridVariables> random() {
        return StateTunnels.of(
                getRand(properties.minMaxX()),
                getRand(properties.minMaxY()),
                properties);
    }


    @Override
    public StateI<GridVariables> clip() {
        var minMaxX = properties.minMaxX();
        var xClipped = MathUtils.clip(variables.x(), minMaxX.getFirst(), minMaxX.getSecond());
        var minMaxY = properties.minMaxY();
        var yClipped = MathUtils.clip(variables.y(), minMaxY.getFirst(), minMaxY.getSecond());
        return StateTunnels.of(xClipped, yClipped,properties);
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
