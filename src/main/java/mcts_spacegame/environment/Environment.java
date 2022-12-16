package mcts_spacegame.environment;

import lombok.extern.java.Log;
import mcts_spacegame.enums.Action;
import mcts_spacegame.models_space.SpaceCell;
import mcts_spacegame.models_space.SpaceGrid;
import mcts_spacegame.models_space.State;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * This class models space ship environment. Most right column are goal states.
 */

@Log
public class Environment implements EnvironmentInterface {
    public static final double MOVE_COST = 1d;
    public static final double STILL_COST = 0d;
    public static final double CRASH_COST = 100d;

    SpaceGrid spaceGrid;

    public Environment(SpaceGrid spaceGrid) {
        this.spaceGrid = spaceGrid;
    }

    @Override
    public StepReturn step(Action action, State oldPosition) {
        Optional<SpaceCell> cellPresentOpt = spaceGrid.getCell(oldPosition);

        if (cellPresentOpt.isEmpty()) {  //if empty, position not defined, assume crash
            return StepReturn.builder()
                    .newPosition(oldPosition).isTerminal(true).isFail(true).reward(-CRASH_COST)
                    .build();
        }
        State newPosition = getNewPosition(action, oldPosition);
        SpaceCell cellNew = spaceGrid.getCell(newPosition).orElse(cellPresentOpt.get());
        boolean isCrashingIntoWall = isOnLowerBorderAndDownOrUpperBorderAndUp(action, cellPresentOpt.get());
        boolean isCrashingIntoObstacle = cellNew.isObstacle;
        boolean isMovingIntoGoal = cellNew.isGoal;
        boolean isCrashing = isCrashingIntoWall || isCrashingIntoObstacle;
        boolean isTerminal = isCrashing || isMovingIntoGoal;
        double costMotion = (action.equals(Action.still)) ? STILL_COST : MOVE_COST;
        double penaltyCrash = (isCrashing) ? CRASH_COST : STILL_COST;
        double reward = -costMotion - penaltyCrash;

        //todo för in logic ovan i builder
        return StepReturn.builder()
                .newPosition(newPosition).isTerminal(isTerminal).isFail(isCrashing).reward(reward)
                .build();
    }

    private boolean isOnLowerBorderAndDownOrUpperBorderAndUp(Action action, SpaceCell cell) {
        return cell.isOnLowerBorder && action.equals(Action.down) ||
                cell.isOnUpperBorder && action.equals(Action.up);
    }

    @NotNull
    private State getNewPosition(Action action, State state) {
        State newPosition = state.copy();

        switch (action) {
            case up:
                newPosition.y++;
                break;
            case down:
                newPosition.y--;
                break;
            case still:
                break;
        }
        newPosition.x++;
        return newPosition;
    }

    @Override
    public String toString() {
        return spaceGrid.toString();
    }

}
