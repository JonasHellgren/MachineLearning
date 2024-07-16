package maze_domain_design.domain.environment.value_objects;

import common.math.MathUtils;
import lombok.ToString;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.math3.util.Pair;

import java.util.Objects;

public record State(
        Integer x, Integer y,
        EnvironmentProperties properties
) {

    public static State of(Integer x, Integer y,EnvironmentProperties p) {
        return new State(x, y,p);
    }

    public static State ofRandom(EnvironmentProperties p) {
        return new State(0,0,p).random();
    }

    public  State random() {
        return new State(
                getRand(properties.minMaxX()),
                getRand(properties.minMaxY()),
                properties);
    }

    public boolean isTerminal() {
        return Objects.equals(x, properties.xTerminal());
    }

    public boolean isFail(EnvironmentProperties properties) {
        return Objects.equals(y, properties.yFailTerminal()) &&
                isTerminal();
    }

    public State clip() {
        var minMaxX = properties.minMaxX();
        var xClipped = MathUtils.clip(x, minMaxX.getFirst(), minMaxX.getSecond());
        var minMaxY = properties.minMaxY();
        var yClipped = MathUtils.clip(y, minMaxY.getFirst(), minMaxY.getSecond());
        return new State(xClipped, yClipped,properties);
    }

    private static int getRand(Pair<Integer, Integer> pair) {
        return RandomUtils.nextInt(pair.getFirst(), pair.getSecond()+1);
    }

    @Override
    public String toString() {
        return "(x,y)=("+x+","+y+")";
    }
}
