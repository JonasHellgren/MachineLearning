package policy_gradient_problems.environments.maze;

import lombok.Builder;
import lombok.With;

import java.awt.geom.Point2D;
import java.util.List;

@Builder
public record MazeSettings(
        int gridWidth, int gridHeight,
        @With double probabilityIntendedDirection,
        double costMove,
        Point2D posTerminalGood, double rewardTerminalGood,
        Point2D posTerminalBad, double rewardTerminalBad
        ) {


    public static MazeSettings newDefault() {
        return MazeSettings.builder()
                .gridHeight(3).gridWidth(4)
                .probabilityIntendedDirection(0.8)
                .costMove(0.04)
                .posTerminalGood(new Point2D.Double(3, 2)).rewardTerminalGood(1)
                .posTerminalBad(new Point2D.Double(3, 1)).rewardTerminalBad(-1)
                .build();
    }


    public List<Point2D> terminalPositions() {
        return List.of(posTerminalGood,posTerminalBad);
    }

}
