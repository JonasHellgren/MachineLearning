package mcts_spacegame.environment;


import common.MathUtils;
import lombok.extern.java.Log;
import mcts_spacegame.enums.Action;
import mcts_spacegame.models.SpaceCell;
import mcts_spacegame.models.SpaceGrid;
import mcts_spacegame.models.State;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@Log
public class Environment implements EnvironmentInterface {

    public static final double MOVE_COST = 1d;
    public static final double STILL_COST=0d;
    public static final double CRASH_COST = 100d;
    SpaceGrid spaceGrid;

    public Environment(SpaceGrid spaceGrid) {
        this.spaceGrid = spaceGrid;
    }

    @Override
    public StepReturn step(Action action, State oldPosition) {
        Optional<SpaceCell> cellPresentOpt = spaceGrid.getCell(oldPosition);

        if (cellPresentOpt.isEmpty()) {  //if present position not is defined, assume crash
            return new StepReturn(oldPosition,true,-CRASH_COST);
        }
        State newPosition = getNewPosition(action, oldPosition);

        Optional<SpaceCell> cellNewOpt = spaceGrid.getCell(newPosition);
        if (cellNewOpt.isEmpty()) {
            log.info("new position is outside grid, assume it to be equal old position");
            newPosition=oldPosition;
            cellNewOpt=cellPresentOpt;
        }

        boolean isCrashingIntoWall =
                cellPresentOpt.get().isOnLowerBorder && action.equals(Action.down) ||
                        cellPresentOpt.get().isOnUpperBorder && action.equals(Action.up);
        boolean isCrashingIntoObstacle = cellNewOpt.get().isObstacle;
        boolean isGoal = cellPresentOpt.get().isGoal;
        boolean isCrashing = isCrashingIntoWall || isCrashingIntoObstacle;
        boolean isTerminal = isCrashing || isGoal;
        double costMotion = (action.equals(Action.still)) ? STILL_COST : MOVE_COST;
        double penaltyCrash = (isCrashing) ? CRASH_COST : STILL_COST;
        double reward = -costMotion - penaltyCrash;

        return new StepReturn(newPosition, isTerminal, reward);
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

        newPosition.y= MathUtils.clip(newPosition.y,0, spaceGrid.getNofRows()-1);
        newPosition.x++;
        return newPosition;
    }

    @Override
    public String toString() {
        return spaceGrid.toString();
    }

}
