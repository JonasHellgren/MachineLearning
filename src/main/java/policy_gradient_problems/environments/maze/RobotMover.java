package policy_gradient_problems.environments.maze;

import common.BucketLimitsHandler;
import common.IndexFinder;
import common.MathUtils;
import common.RandUtils;
import lombok.AllArgsConstructor;
import org.bytedeco.opencv.opencv_core.Point2d;
import policy_gradient_problems.domain.abstract_classes.Action;
import java.util.List;
import java.util.Map;
import static common.ListUtils.toArray;

@AllArgsConstructor
public class RobotMover {
    MazeSetting settings;

    final Map<Integer,Point2d> actionDPosMap=Map.of(
            0,new Point2d(0,1),  //up
            1,new Point2d(1,0),  //right
            2,new Point2d(0,-1), //down
            3,new Point2d(-1,0));  //left

    final List<Double> limits=BucketLimitsHandler.getLimits(List.of(0.8,0.1,0.1));

    // Method to calculate new intended positions
    public Point2d newIntentedPos(Point2d pos, Action action) {
        Point2d dPos=actionDPosMap.get(action.asInt());
        int bucket= IndexFinder.findBucket(toArray(limits),RandUtils.randomNumberBetweenZeroAndOne());
        return switch(bucket) {
            case 0 -> new Point2d(pos.x()+dPos.x(), pos.y()+dPos.y());  // Intended direction
            case 2 -> new Point2d(pos.x()+dPos.y(), pos.y()-dPos.x());  // +90 degrees rotation (Clockwise)
            case 3 -> new Point2d(pos.x()-dPos.y(), pos.y()+dPos.x());  // -90 degrees rotation (Counter-Clockwise)
            default -> throw new IllegalStateException("Unexpected value: " + bucket);
        };
    }

    public Point2d adjustNewPosition(Point2d posOld,Point2d posNew) {
        // Adjust position if it's outside grid boundaries or hits a wall
        int x = (int) MathUtils.clip(posNew.x(), 0, settings.gridWidth() - 1); // Clip x within [0, gridWidth - 1]
        int y = (int) MathUtils.clip(posNew.y(), 0, settings.gridHeight() - 1); // Clip y within [0, gridHeight - 1]

        // Additional logic for walls inside the grid, reset to previous position
        if (x == 1 && y == 1) {
            return posOld;
        }

        return new Point2d(x,y);
    }

}
