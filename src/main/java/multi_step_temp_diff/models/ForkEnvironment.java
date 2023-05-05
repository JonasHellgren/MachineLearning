package multi_step_temp_diff.models;

import common.Conditionals;
import multi_step_temp_diff.interfaces.EnvironmentInterface;

import java.util.Set;
import java.util.function.Predicate;

public class ForkEnvironment implements EnvironmentInterface {

    public static final double R_HEAVEN = 10;
    public static final double R_HELL = -10;
    public static final double R_MOVE = 0;
    public static final int NOF_ACTIONS = 2;
    public static final int NOF_STATES = 15;

    @Override
    public StepReturn step(int state,int action) {
        throwIfBadArgument(state,action);
        final int newState = getNewState(state, action);
        return StepReturn.builder()
                .isNewStateTerminal(isTerminalState(newState))
                .newState(newState)
                .reward(getReward(newState))
                .build();
    }

    private void throwIfBadArgument(int state, int action) {
        Predicate<Integer> isNonValidAction = (a) -> a > NOF_ACTIONS - 1;
        Predicate<Integer> isNonValidState = (a) -> a > NOF_STATES - 1;

        Conditionals.executeIfTrue(isNonValidAction.test(action), () ->
        {
            throw new IllegalArgumentException("Non valid action");
        });

        Conditionals.executeIfTrue(isNonValidState.test(state), () ->
        {
            throw new IllegalArgumentException("Non valid state");
        });
    }

    private int getNewState(int state, int action) {
        boolean isSplit = (state == 5);
        return (isSplit)
                ? (action == 0) ? 6 : 7
                : state + 1;
    }

    private double getReward(int state) {
        return (isTerminalState(state))
                ? (state == 10) ? R_HEAVEN : R_HELL
                : R_MOVE;
    }

    @Override
    public boolean isTerminalState(int state) {
        return (state == 15 || state == 10);
    }

    @Override
    public Set<Integer> actionSet(int state) {
        return Set.of(0,1);
    }
}
