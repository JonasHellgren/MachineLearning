package policy_gradient_problems.environments.maze;

import com.google.common.base.Preconditions;
import policy_gradient_problems.domain.abstract_classes.Action;
import policy_gradient_problems.domain.abstract_classes.EnvironmentI;
import policy_gradient_problems.domain.abstract_classes.StateI;
import policy_gradient_problems.domain.value_classes.StepReturn;

public class EnvironmentMaze implements EnvironmentI<VariablesMaze> {

    MazeSettings settings;
    RobotMover mover;

    public static EnvironmentMaze newDefault() {
        return new EnvironmentMaze(MazeSettings.newDefault());
    }

    public EnvironmentMaze(MazeSettings settings) {
        this.settings = settings;
        this.mover=new RobotMover(settings);
    }

    public StepReturn<VariablesMaze> step(StateI<VariablesMaze> state0, Action action) {
        StateMaze state=(StateMaze) state0;
        StateMaze stateNew=StateMaze.newFromPoint(mover.newPos(state.point(),action));
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
        Preconditions.checkArgument(isTerminal(state),"Not terminal state");
        return settings.posTerminalGood().equals(state.point())
                ? settings.rewardTerminalGood()
                : settings.rewardTerminalBad();
    }

    private boolean isTerminal(StateI<VariablesMaze> state) {
        return settings.terminalPositions().contains(state.getVariables().pos());
    }
}
