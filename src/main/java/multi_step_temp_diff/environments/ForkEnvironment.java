package multi_step_temp_diff.environments;

import common.Conditionals;
import multi_step_temp_diff.interfaces.EnvironmentInterface;
import multi_step_temp_diff.models.StepReturn;

import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ForkEnvironment implements EnvironmentInterface {

    public static final double R_HEAVEN = 10;
    public static final double R_HELL = -10;
    public static final double R_MOVE = 0;
    public static final int NOF_ACTIONS = 2;
    public static final int NOF_STATES = 16;
    private static final int STATE_HEAVEN = 10;
    private static final int STATE_HELL = 15;

    @Override
    public StepReturn step(int state, int action) {
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

        if (state==6) {
            return 11;
        }

        boolean isSplit = (state == 5);
        return (isSplit)
                ? (action == 0) ? 6 : 7
                : state + 1;
    }

    private double getReward(int newState) {
        return (isTerminalState(newState))
                ? (newState == STATE_HEAVEN) ? R_HEAVEN : R_HELL
                : R_MOVE;
    }

    @Override
    public boolean isTerminalState(int state) {
        return (state == STATE_HELL || state == STATE_HEAVEN);
    }

    @Override
    public Set<Integer> actionSet() {
        return IntStream.range(0, NOF_ACTIONS).boxed().collect(Collectors.toSet());
    }

    @Override
    public Set<Integer> stateSet() {
        return IntStream.range(0, NOF_STATES).boxed().collect(Collectors.toSet());
    }
}
