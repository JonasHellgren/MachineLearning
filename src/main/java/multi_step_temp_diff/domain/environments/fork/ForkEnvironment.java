package multi_step_temp_diff.domain.environments.fork;

import common.Conditionals;
import common.SetUtils;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.environment_abstract.StepReturn;
import multi_step_temp_diff.domain.environment_valueobj.ForkEnvironmentSettings;

import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ForkEnvironment implements EnvironmentInterface<ForkVariables> {

    public static ForkEnvironmentSettings settings=ForkEnvironmentSettings.getDefault();

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
        Predicate<Integer> isNonValidAction = (a) -> a > settings.nofActions() - 1;
        Predicate<StateInterface<ForkVariables> > isNonValidState =
                (s) -> s.getVariables().position > settings.nofStates() - 1;

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
        if (pos==settings.posStartHell()) {
            return new ForkState(new ForkVariables(settings.posAfterStartHell()));
        }

        boolean isSplit = (pos == settings.posSplit());
        int newPos= (isSplit)
                ? (action == 0) ? settings.posStartHell() : settings.posStartHeaven()
                : pos + 1;
        return new ForkState(new ForkVariables(newPos));
    }

    private double getReward(StateInterface<ForkVariables> newState) {
        return (isTerminalState(newState))
                ? getRewardAtTerminal(newState)
                : settings.rewardMove();
    }

    private double getRewardAtTerminal(StateInterface<ForkVariables> newState) {
        return (ForkState.getPos.apply(newState).equals(settings.posHeaven()))
                ? settings.rewardHeaven() : settings.rewardHell();
    }

    @Override
    public boolean isTerminalState(StateInterface<ForkVariables>  state) {
        return (ForkState.getPos.apply(state).equals(settings.posHell()) ||
                ForkState.getPos.apply(state).equals(settings.posHeaven()));
    }

    @Override
    public Set<Integer> actionSet() {
        return SetUtils.getSetFromRange(0, settings.nofActions());
    }

    @Override
    public  Set<StateInterface<ForkVariables>> stateSet() {
        return IntStream.range(0, settings.nofStates()).boxed().map(ForkState::newFromPos).collect(Collectors.toSet());
    }
}
