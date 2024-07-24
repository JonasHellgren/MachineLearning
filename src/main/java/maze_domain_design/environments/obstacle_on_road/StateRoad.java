package maze_domain_design.environments.obstacle_on_road;

import common.math.MathUtils;
import maze_domain_design.domain.environment.value_objects.StateI;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.math3.util.Pair;

import java.util.Objects;

public record StateRoad  (
                               GridVariables variables,
                               PropertiesRoad properties
) implements StateI<GridVariables> {

    public static  StateRoad of(Integer x, Integer y, PropertiesRoad p) {
        return new StateRoad(GridVariables.of(x,y),p);
    }

    public static  StateRoad ofRandom(PropertiesRoad p) {
        return new StateRoad(GridVariables.of(0,0),p).random();
    }

    @Override
    public GridVariables getVariables() {
        return variables;
    }

    @Override
    public StateRoad random() {
        return StateRoad.of(
                getRand(properties.minMaxX()),
                getRand(properties.minMaxY()),
                properties);
    }


    @Override
    public StateRoad clip() {
        var minMaxX = properties.minMaxX();
        var xClipped = MathUtils.clip(variables.x(), minMaxX.getFirst(), minMaxX.getSecond());
        var minMaxY = properties.minMaxY();
        var yClipped = MathUtils.clip(variables.y(), minMaxY.getFirst(), minMaxY.getSecond());
        return StateRoad.of(xClipped, yClipped,properties);
    }

    public boolean isTerminal() {
        return Objects.equals(variables.x(), properties.xTerminal());
    }

    public boolean isFail(PropertiesRoad properties) {
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
