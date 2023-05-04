package multi_step_temp_diff.models;

import multi_step_temp_diff.interfaces.EnvironmentInterface;

public class ForkEnvironment implements EnvironmentInterface {

    private static final int R_HEAVEN = 10;
    private static final int R_HELL = -10;
    private static final int R_MOVE = 0;

    @Override
    public StepReturn step(int action, int state) {
        return StepReturn.builder()
                .isTerminal(isTerminalState(state))
                .state(getNewState(action, state))
                .reward(getReward(getNewState(action, state)))
                .build();
    }

    private int getNewState(int action, int state) {
        boolean isSplit=(state == 5);
        return (isSplit)
                        ? (action == 0) ? 6: 7
                        : state +1;
    }

    private int getReward(int state) {
        return (isTerminalState(state))
                ? (state== 10) ? R_HEAVEN: R_HELL
                : R_MOVE;
    }

    @Override
    public boolean isTerminalState(int state) {
        return (state==15 || state==10);
    }
}
