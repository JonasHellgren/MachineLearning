package policy_gradient_problems.environments.maze;
import java.awt.geom.Point2D;

import common.BucketLimitsHandler;
import common.IndexFinder;
import common.MathUtils;
import common.RandUtils;
import policy_gradient_problems.domain.abstract_classes.Action;

import java.util.List;
import java.util.Map;

import static common.ListUtils.toArray;

/***
 * newIntendedPos, method to calculate new intended position
 * adjustNewPosition, if needed, adjust new intended position
 */

public class RobotMover {
    MazeSetting settings;
    final List<Double> limits;

    final Map<Integer, Point2D> actionDPosMap = Map.of(
            Direction.up.getInt(), Direction.up.direction(),
            Direction.right.getInt(), Direction.right.direction(),
            Direction.down.getInt(), Direction.down.direction(),
            Direction.left.getInt(), Direction.left.direction());

    public RobotMover(MazeSetting settings) {
        this.settings = settings;
        this.limits = BucketLimitsHandler.getLimits(List.of(
                settings.probabilityIntendedDirection(),
                (1 - settings.probabilityIntendedDirection()) / 2,
                (1 - settings.probabilityIntendedDirection()) / 2));
    }


    public Point2D newPos(Point2D pos, Action action) {
        var intendedPos=newIntendedPos(pos,action);
        return adjustNewPosition(pos,intendedPos);
    }

    public Point2D newIntendedPos(Point2D pos, Action action) {
        Point2D dPos = actionDPosMap.get(action.asInt());
        int bucket = IndexFinder.findBucket(toArray(limits), RandUtils.randomNumberBetweenZeroAndOne());
        return switch (bucket) {
            case 0 -> new Point2D.Double(pos.getX() + dPos.getX(),pos.getY() + dPos.getY());  // Intended direction
            case 1 -> new Point2D.Double(pos.getX() + dPos.getY(),pos.getY() - dPos.getX());  // +90 degrees rotation (Clockwise)
            case 2 -> new Point2D.Double(pos.getX() - dPos.getY(),pos.getY() + dPos.getX());  // -90 degrees rotation (Counter-Clockwise)
            default -> throw new IllegalStateException("Unexpected value: " + bucket);
        };
    }

    public Point2D adjustNewPosition(Point2D posOld, Point2D posNew) {
        // Adjust position if it's outside grid boundaries or hits a wall
        int x = (int) MathUtils.clip(posNew.getX(), 0, settings.gridWidth() - 1); // Clip x within [0, gridWidth - 1]
        int y = (int) MathUtils.clip(posNew.getY(), 0, settings.gridHeight() - 1); // Clip y within [0, gridHeight - 1]

        return (x == 1 && y == 1)
                ? posOld  //hitting walls inside the grid, reset to previous position
                : new Point2D.Double(x, y);
    }

}

