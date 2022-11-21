package mcts_spacegame.environment;


import common.MathUtils;
import mcts_spacegame.enums.Action;
import mcts_spacegame.models.SpaceCell;
import mcts_spacegame.models.SpaceGrid;
import mcts_spacegame.models.State;
import org.jetbrains.annotations.NotNull;


public class Environment implements EnvironmentInterface {

    public static final double MOVE_COST = 1d;
    public static final double CRASH_COST = 100d;
    SpaceGrid spaceGrid;

    public Environment(SpaceGrid spaceGrid) {
        this.spaceGrid = spaceGrid;
    }

    @Override
    public StepReturn step(Action action, State state) {
        SpaceCell cellPresent = spaceGrid.getCell(state);

        boolean isCrashingIntoWall =
                cellPresent.isOnLowerBorder && action.equals(Action.down) ||
                        cellPresent.isOnUpperBorder && action.equals(Action.up);

        State newPosition = getNewPosition(action, state);
        boolean isCrashingIntoObstacle = spaceGrid.getCell(newPosition).isObstacle;
        boolean isGoal = spaceGrid.getCell(newPosition).isGoal;
        boolean isCrashing = isCrashingIntoWall || isCrashingIntoObstacle;
        boolean isTerminal = isCrashing || isGoal;
        double costMotion = (action.equals(Action.still)) ? 0 : MOVE_COST;
        double penaltyCrash = (isCrashing) ? CRASH_COST : 0;
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
}
