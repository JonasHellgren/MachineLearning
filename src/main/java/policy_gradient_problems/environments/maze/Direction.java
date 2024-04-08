package policy_gradient_problems.environments.maze;

import common.ListUtils;
import common.RandUtils;
import lombok.AllArgsConstructor;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public enum Direction {
    up(0, new Point2D.Double(0.0, 1.0)),
    right(1, new Point2D.Double(1.0, 0.0)),
    down(2, new Point2D.Double(0.0, -1.0)),
    left(3, new Point2D.Double(-1.0, 0.0));

    private final int intValue;
    private final Point2D.Double direction;

    public static  Direction random() {
        List<Direction> directionList=List.of(up,right,down,left);
        RandUtils<Direction> randUtils=new RandUtils<>();
        return randUtils.getRandomItemFromList(directionList);
    }

    // Getter for the integer value
    public int getInt() {
        return intValue;
    }

    // Getter for the Point2D value
    public Point2D.Double direction() {
        return direction;
    }

}
