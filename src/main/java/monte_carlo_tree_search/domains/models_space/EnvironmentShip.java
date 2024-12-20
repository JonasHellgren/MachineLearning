package monte_carlo_tree_search.domains.models_space;

import lombok.Getter;
import lombok.extern.java.Log;
import monte_carlo_tree_search.models_and_support_classes.StepReturnGeneric;
import monte_carlo_tree_search.interfaces.ActionInterface;
import monte_carlo_tree_search.interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.interfaces.StateInterface;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * This class models space ship environment. Most right column are goal states.
 */

@Log
@Getter
public class EnvironmentShip implements EnvironmentGenericInterface<ShipVariables,ShipActionSet> {
    public static final double MOVE_COST = 1d;
    public static final double STILL_COST = 0d;
    public static final double CRASH_COST = 100d;

    SpaceGrid spaceGrid;

    public EnvironmentShip(SpaceGrid spaceGrid) {
        this.spaceGrid = spaceGrid;
    }

    @Override
    public StepReturnGeneric<ShipVariables> step(ActionInterface<ShipActionSet> action,
                                                 StateInterface<ShipVariables> oldPosition) {
        Optional<SpaceCell> cellPresentOpt = spaceGrid.getCell(oldPosition);

        if (cellPresentOpt.isEmpty()) {  //if empty, position not defined, assume crash
            return StepReturnGeneric.<ShipVariables>builder()
                    .newState(oldPosition).isTerminal(true).isFail(true).reward(-CRASH_COST)
                    .build();
        }
        StateInterface<ShipVariables> newPosition = getNewPosition(action, oldPosition);

        SpaceCell cellNew = spaceGrid.getCell(newPosition).orElse(cellPresentOpt.get());
        boolean isCrashingIntoWall = isOnLowerBorderAndDownOrUpperBorderAndUp(action, cellPresentOpt.get());
        boolean isCrashingIntoObstacle = cellNew.isObstacle;
        double bonus=cellNew.bonus;
        boolean isMovingIntoGoal = cellNew.isGoal;
        boolean isCrashing = isCrashingIntoWall || isCrashingIntoObstacle;
        boolean isTerminal = isCrashing || isMovingIntoGoal;
        double costMotion = (action.getValue().equals(ShipActionSet.still)) ? STILL_COST : MOVE_COST;
        double penaltyCrash = (isCrashing) ? CRASH_COST : STILL_COST;
        double reward = -costMotion - penaltyCrash+bonus;

        //todo för in logic ovan i builder
        return StepReturnGeneric.<ShipVariables>builder()
                .newState(newPosition).isTerminal(isTerminal).isFail(isCrashing).reward(reward)
                .build();
    }

    private boolean isOnLowerBorderAndDownOrUpperBorderAndUp(ActionInterface<ShipActionSet> action, SpaceCell cell) {
        return cell.isOnLowerBorder && action.getValue().equals(ShipActionSet.down) ||
                cell.isOnUpperBorder && action.getValue().equals(ShipActionSet.up);
    }

    @NotNull
    private StateInterface<ShipVariables> getNewPosition(ActionInterface<ShipActionSet> action, StateInterface<ShipVariables> state) {
        StateInterface<ShipVariables> newPosition = state.copy();
        ShipVariables newVars=newPosition.getVariables();

        switch (action.getValue()) {
            case up:
                newVars.y++;
                break;
            case down:
                newVars.y--;
                break;
            case still:
                break;
        }
        newVars.x++;
        return newPosition;
    }

    @Override
    public String toString() {
        return spaceGrid.toString();
    }

}
