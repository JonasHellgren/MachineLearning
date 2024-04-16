package policy_gradient_problems.environments.maze;

import common.other.RandUtils;
import lombok.AllArgsConstructor;
import java.awt.geom.Point2D;
import java.util.List;

@AllArgsConstructor
public enum Direction {
    UP(0, new Point2D.Double(0.0, 1.0)),
    RIGHT(1, new Point2D.Double(1.0, 0.0)),
    DOWN(2, new Point2D.Double(0.0, -1.0)),
    LEFT(3, new Point2D.Double(-1.0, 0.0));

    private final int intValue;
    private final Point2D.Double dir;

    public static  Direction random() {
        List<Direction> directionList=List.of(UP, RIGHT, DOWN, LEFT);
        RandUtils<Direction> randUtils=new RandUtils<>();
        return randUtils.getRandomItemFromList(directionList);
    }

    public int getInt() {
        return intValue;
    }

    public Point2D.Double direction() {
        return dir;
    }

}
