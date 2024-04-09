package policy_gradient_problems.environments.maze;
import java.awt.geom.Point2D;

import common.BucketLimitsHandler;
import common.IndexFinder;
import common.RandUtils;
import policy_gradient_problems.domain.abstract_classes.Action;

import java.util.List;
import java.util.Map;

import static common.ListUtils.toArray;
import static common.MathUtils.clip;

/***
 * newIntendedPos, method to calculate new intended position
 * adjustNewPosition, if needed, adjust new intended position
 */

public class RobotMover {
    MazeSettings settings;
    final List<Double> probLimits;

    final Map<Integer, Point2D> actionDPosMap = Map.of(
            Direction.UP.getInt(), Direction.UP.direction(),
            Direction.RIGHT.getInt(), Direction.RIGHT.direction(),
            Direction.DOWN.getInt(), Direction.DOWN.direction(),
            Direction.LEFT.getInt(), Direction.LEFT.direction());

    public RobotMover(MazeSettings settings) {
        this.settings = settings;
        double probNotIntended = (1 - settings.probabilityIntendedDirection()) / 2;
        this.probLimits = BucketLimitsHandler.getLimits(List.of(
                settings.probabilityIntendedDirection(), probNotIntended, probNotIntended));
    }


    public Point2D newPos(Point2D pos, Action action) {
        var intendedPos=newIntendedPos(pos,action);
        return adjustNewPosition(pos,intendedPos);
    }

    public Point2D newIntendedPos(Point2D pos, Action action) {
        Point2D dPos = actionDPosMap.get(action.asInt());
        int bucket = IndexFinder.findBucket(toArray(probLimits), RandUtils.randomNumberBetweenZeroAndOne());
        return switch (bucket) {
            case 0 -> new Point2D.Double(pos.getX() + dPos.getX(),pos.getY() + dPos.getY());  // Intended direction
            case 1 -> new Point2D.Double(pos.getX() + dPos.getY(),pos.getY() - dPos.getX());  // +90 degrees rotation (Clockwise)
            case 2 -> new Point2D.Double(pos.getX() - dPos.getY(),pos.getY() + dPos.getX());  // -90 degrees rotation (Counter-Clockwise)
            default -> throw new IllegalStateException("Unexpected value: " + bucket);
        };
    }

    public Point2D adjustNewPosition(Point2D posOld, Point2D posNew) {
        int x = (int) clip(posNew.getX(), 0, (double) settings.gridWidth() - 1); // Clip x [0, gridWidth - 1]
        int y = (int) clip(posNew.getY(), 0, (double) settings.gridHeight() - 1); // Clip y [0, gridHeight - 1]

        return (x == 1 && y == 1)
                ? posOld  //hitting walls inside the grid, reset to previous position
                : new Point2D.Double(x, y);
    }

}

