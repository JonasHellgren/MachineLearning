package maze_domain_design.domain.environment.value_objects;

import common.math.MathUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.math3.util.Pair;

import java.util.Objects;

public record State(
        Integer x, Integer y
) {

    public static State of(Integer x, Integer y) {
        return new State(x, y);
    }


    public static State random(EnvironmentProperties properties) {
        return new State(
                getRand(properties.minMaxX()),
                getRand(properties.minMaxY()));
    }

    public boolean isTerminal(EnvironmentProperties properties) {
        return Objects.equals(x, properties.xTerminal());
    }

    public boolean isFail(EnvironmentProperties properties) {
        return Objects.equals(y, properties.yFailTerminal()) &&
                isTerminal(properties);
    }

    public State clip(EnvironmentProperties properties) {
        var minMaxX = properties.minMaxX();
        var xClipped = MathUtils.clip(x, minMaxX.getFirst(), minMaxX.getSecond());
        var minMaxY = properties.minMaxY();
        var yClipped = MathUtils.clip(y, minMaxY.getFirst(), minMaxY.getSecond());
        return new State(xClipped, yClipped);
    }

    private static int getRand(Pair<Integer, Integer> pair) {
        return RandomUtils.nextInt(pair.getFirst(), pair.getSecond()+1);
    }
}
