package multi_step_temp_diff.environments;

import common.Conditionals;
import common.SetUtils;
import multi_step_temp_diff.interfaces_and_abstract.EnvironmentInterface;
import multi_step_temp_diff.interfaces_and_abstract.StateInterface;
import multi_step_temp_diff.models.StepReturn;

import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ForkEnvironment implements EnvironmentInterface<ForkVariables> {

    public static final double R_HEAVEN = 10, R_HELL = -10, R_MOVE = 0;
    public static final int NOF_ACTIONS = 2, NOF_STATES = 16;
    private static final int HEAVEN = 10, HELL = 15, SPLIT = 5, START_TO_HELL = 6;
    public static final int START_TO_HEAVEN = 7, AFTER_START_TO_HELL = 11;

    @Override
    public StepReturn<ForkVariables> step(StateInterface<ForkVariables> state, int action) {
        throwIfBadArgument(state,action);
        final StateInterface<ForkVariables> newState = getNewState(state, action);
        return StepReturn.<ForkVariables>builder()
                .isNewStateTerminal(isTerminalState(newState))
                .newState(newState)
                .reward(getReward(newState))
                .build();
    }

    private void throwIfBadArgument(StateInterface<ForkVariables> state, int action) {
        Predicate<Integer> isNonValidAction = (a) -> a > NOF_ACTIONS - 1;
        Predicate<StateInterface<ForkVariables> > isNonValidState = (s) -> s.getVariables().position > NOF_STATES - 1;

        Conditionals.executeIfTrue(isNonValidAction.test(action), () ->
        {
            throw new IllegalArgumentException("Non valid action");
        });

        Conditionals.executeIfTrue(isNonValidState.test(state), () ->
        {
            throw new IllegalArgumentException("Non valid state");
        });
    }

    private StateInterface<ForkVariables> getNewState(StateInterface<ForkVariables>  state, int action) {

        int pos=state.getVariables().position;
        if (pos==START_TO_HELL) {
            return new ForkState(new ForkVariables(AFTER_START_TO_HELL));
        }

        boolean isSplit = (pos == SPLIT);
        int newPos= (isSplit)
                ? (action == 0) ? START_TO_HELL : START_TO_HEAVEN
                : pos + 1;
        return new ForkState(new ForkVariables(newPos));
    }

    private double getReward(StateInterface<ForkVariables> newState) {
        return (isTerminalState(newState))
                ? (ForkState.getPos.apply(newState) == HEAVEN) ? R_HEAVEN : R_HELL
                : R_MOVE;
    }

    @Override
    public boolean isTerminalState(StateInterface<ForkVariables>  state) {
        return (ForkState.getPos.apply(state) == HELL || ForkState.getPos.apply(state) == HEAVEN);
    }

    @Override
    public Set<Integer> actionSet() {
        return SetUtils.getSetFromRange(0,NOF_ACTIONS);
    }

    @Override
    public  Set<StateInterface<ForkVariables>> stateSet() {
        return IntStream.range(0, NOF_STATES).boxed().map(ForkState::newFromPos).collect(Collectors.toSet());
    }
}
