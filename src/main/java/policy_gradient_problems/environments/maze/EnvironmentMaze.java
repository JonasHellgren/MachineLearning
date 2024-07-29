package policy_gradient_problems.environments.maze;

import lombok.Getter;
import policy_gradient_problems.domain.abstract_classes.Action;
import policy_gradient_problems.domain.abstract_classes.EnvironmentI;
import policy_gradient_problems.domain.abstract_classes.StateI;
import policy_gradient_problems.domain.value_classes.StepReturn;

import static org.nd4j.shade.guava.base.Preconditions.checkArgument;

@Getter
public class EnvironmentMaze implements EnvironmentI<VariablesMaze> {

    MazeSettings settings;
    RobotMover mover;

    public static EnvironmentMaze newDefault() {
        return new EnvironmentMaze(MazeSettings.newDefault());
    }

    public static EnvironmentMaze newOneRowMoveAsIntended() {
        return new EnvironmentMaze(MazeSettings.newOneRowMoveAsIntended());
    }


    public static EnvironmentMaze new4x3MoveAsIntended() {
        return new EnvironmentMaze(MazeSettings.new4x3MoveAsIntended());
    }


    public EnvironmentMaze(MazeSettings settings) {
        this.settings = settings;
        this.mover = new RobotMover(settings);
    }

    public StepReturn<VariablesMaze> step(StateI<VariablesMaze> state0, Action action) {
        var state = (StateMaze) state0;
        var stateNew = StateMaze.newFromPoint(mover.newPos(state.point(), action));
        boolean isTerminal = isTerminal(stateNew);
        double reward = isTerminal
                ? rewardAtTerminal(stateNew)
                : -settings.costMove();
        return StepReturn.<VariablesMaze>builder()
                .reward(reward)
                .state(stateNew)
                .isTerminal(isTerminal)
                .isFail(false)
                .build();
    }

    private double rewardAtTerminal(StateMaze state) {
        checkArgument(isTerminal(state), "Not terminal stateNew");
        return settings.posTerminalGood().equals(state.point())
                ? settings.rewardTerminalGood()
                : settings.rewardTerminalBad();
    }

    private boolean isTerminal(StateI<VariablesMaze> state) {
        return settings.isTerminal(state);
    }
}
