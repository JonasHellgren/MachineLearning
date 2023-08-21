package multi_step_temp_diff.domain.environments.fork;

import common.Conditionals;
import common.MySetUtils;
import lombok.Setter;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.environment_abstract.StepReturn;
import multi_step_temp_diff.domain.environment_valueobj.ForkEnvironmentSettings;

import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Setter
public class ForkEnvironment implements EnvironmentInterface<ForkVariables> {

    public static ForkEnvironmentSettings envSettings =ForkEnvironmentSettings.getDefault();

    @Override
    public StepReturn<ForkVariables> step(StateInterface<ForkVariables> state, int action) {
        throwIfBadArgument(state,action);
        final StateInterface<ForkVariables> newState = getNewState(state, action);
        return StepReturn.<ForkVariables>builder()
                .isNewStateTerminal(isTerminalState(newState))
                .newState(newState)
                .reward(getReward(newState))
                .isNewStateFail(false)
                .build();
    }

    private void throwIfBadArgument(StateInterface<ForkVariables> state, int action) {
        Predicate<Integer> isNonValidAction = (a) -> a > envSettings.nofActions() - 1;
        Predicate<StateInterface<ForkVariables> > isNonValidState =
                (s) -> s.getVariables().position > envSettings.nofStates() - 1;

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
        if (pos== envSettings.posStartHell()) {
            return new ForkState(new ForkVariables(envSettings.posAfterStartHell()));
        }

        boolean isSplit = (pos == envSettings.posSplit());
        int newPos= (isSplit)
                ? (action == 0) ? envSettings.posStartHell() : envSettings.posStartHeaven()
                : pos + 1;
        return new ForkState(new ForkVariables(newPos));
    }

    private double getReward(StateInterface<ForkVariables> newState) {
        return (isTerminalState(newState))
                ? getRewardAtTerminal(newState)
                : envSettings.rewardMove();
    }

    private double getRewardAtTerminal(StateInterface<ForkVariables> newState) {
        return (ForkState.getPos.apply(newState).equals(envSettings.posHeaven()))
                ? envSettings.rewardHeaven() : envSettings.rewardHell();
    }

    @Override
    public boolean isTerminalState(StateInterface<ForkVariables>  state) {
        return (ForkState.getPos.apply(state).equals(envSettings.posHell()) ||
                ForkState.getPos.apply(state).equals(envSettings.posHeaven()));
    }

    @Override
    public Set<Integer> actionSet() {
        return MySetUtils.getSetFromRange(0, envSettings.nofActions());
    }

    @Override
    public  Set<StateInterface<ForkVariables>> stateSet() {
        return IntStream.range(0, envSettings.nofStates()).boxed().map(ForkState::newFromPos).collect(Collectors.toSet());
    }
}
