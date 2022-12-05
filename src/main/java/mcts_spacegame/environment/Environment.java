package mcts_spacegame.environment;


import common.MathUtils;
import lombok.extern.java.Log;
import mcts_spacegame.enums.Action;
import mcts_spacegame.models_space.SpaceCell;
import mcts_spacegame.models_space.SpaceGrid;
import mcts_spacegame.models_space.State;
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
            return StepReturn.builder()
                    .newPosition(oldPosition).isTerminal(true).isFail(true).reward(-CRASH_COST)
                    .build();
        }
        State newPosition = getNewPosition(action, oldPosition);
        SpaceCell cellNew=spaceGrid.getCell(newPosition).orElse(cellPresentOpt.get());
          boolean isCrashingIntoWall =
                cellPresentOpt.get().isOnLowerBorder && action.equals(Action.down) ||
                        cellPresentOpt.get().isOnUpperBorder && action.equals(Action.up);
        boolean isCrashingIntoObstacle = cellNew.isObstacle; //if new cell not exists use present
        boolean isMovingIntoGoal = cellNew.isGoal;
        boolean isCrashing = isCrashingIntoWall || isCrashingIntoObstacle;
        boolean isTerminal = isCrashing || isMovingIntoGoal;
        double costMotion = (action.equals(Action.still)) ? STILL_COST : MOVE_COST;
        double penaltyCrash = (isCrashing) ? CRASH_COST : STILL_COST;
        double reward = -costMotion - penaltyCrash;
        return StepReturn.builder()
                .newPosition(newPosition).isTerminal(isTerminal).isFail(isCrashing).reward(reward)
                .build();
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
