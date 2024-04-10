package policy_gradient_problems.environments.maze;

import lombok.Builder;
import lombok.With;
import policy_gradient_problems.domain.abstract_classes.StateI;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

@Builder
public record MazeSettings(
        int gridWidth, int gridHeight,
        @With double probabilityIntendedDirection,
        double costMove,
        Point2D posTerminalGood, double rewardTerminalGood,
        Point2D posTerminalBad, double rewardTerminalBad,
        List<Point2D> obstacles
        ) {


    public static MazeSettings newDefault() {
        return MazeSettings.builder()
                .gridHeight(3).gridWidth(4)
                .probabilityIntendedDirection(0.8)
                .costMove(0.04)
                .posTerminalGood(new Point2D.Double(3, 2)).rewardTerminalGood(1)
                .posTerminalBad(new Point2D.Double(3, 1)).rewardTerminalBad(-1)
                .obstacles(List.of(new Point2D.Double(1, 1)))
                .build();
    }

    public static MazeSettings newOneRow() {
        return MazeSettings.builder()
                .gridHeight(1).gridWidth(4)
                .probabilityIntendedDirection(0.8)
                .costMove(0.04)
                .posTerminalGood(new Point2D.Double(3, 0)).rewardTerminalGood(1)
                .posTerminalBad(new Point2D.Double(3, 1)).rewardTerminalBad(-1)  //outside
                .obstacles(new ArrayList<>())
                .build();
    }

    public boolean isTerminal(StateI<VariablesMaze> state) {
            return terminalPositions().contains(state.getVariables().pos());
    }

    public boolean isObstacle(StateI<VariablesMaze> state) {
        return obstacles().contains(state.getVariables().pos());
    }

    public List<Point2D> terminalPositions() {
        return List.of(posTerminalGood,posTerminalBad);
    }

    public int nNetInputs() {
        return gridWidth+gridHeight;
    }
}
