package safe_rl.domain.trainer.value_objects;

import lombok.Builder;
import lombok.NonNull;
import lombok.With;
import safe_rl.domain.environment.aggregates.StateI;
import safe_rl.domain.environment.value_objects.Action;

import java.util.Optional;

/**
 * The consequence of taking action in state, value is/can be defined later from list of experiences
 */

@Builder
public record Experience<V>(
        @NonNull StateI<V> state,
        @NonNull ActionRewardStateNew<V> ars,
        @With double value,
        @NonNull Optional<ActionRewardStateNew<V>> arsCorrected) {

    public static final int VALUE_DUMMY = 0;
    public static final double REWARD_DUMMY = 0d;

    public static <V> Experience<V> notSafeCorrected(StateI<V> state,
                                                     Action action,
                                                     double reward,
                                                     StateI<V> stateNext,
                                                     Boolean isTerminal
    ) {
        return Experience.<V>builder()
                .state(state)
                .ars(new ActionRewardStateNew<>(action, reward, stateNext, isTerminal))
                .value(VALUE_DUMMY)
                .arsCorrected(Optional.empty())
                .build();
    }

    public static <V> Experience<V> safeCorrected(StateI<V> state,
                                                  Action action,
                                                  Action actionSafeCorrected,
                                                  double reward,
                                                  StateI<V> stateNext,
                                                  Boolean isTerminal
    ) {
        return Experience.<V>builder()
                .state(state)
                .ars(new ActionRewardStateNew<>(action, REWARD_DUMMY, null, false))
                .value(VALUE_DUMMY)
                .arsCorrected(Optional.of(new ActionRewardStateNew<>(actionSafeCorrected,reward,stateNext,isTerminal)))
                .build();
    }


    public boolean isSafeCorrected() {
        return arsCorrected().isPresent();
    }

    public Experience<V> copyWithValue(double value) {
        return this.withValue(value);
    }

    public Action actionApplied() {
        return isSafeCorrected()?arsCorrected.orElseThrow().action():ars.action();
    }

    public StateI<V> stateNextApplied() {
        return isSafeCorrected()?arsCorrected.orElseThrow().stateNext():ars.stateNext();
    }

    public double rewardApplied() {
        return isSafeCorrected()?arsCorrected.orElseThrow().reward():ars.reward();
    }

    public boolean isTerminalApplied() {
        return isSafeCorrected()?arsCorrected.orElseThrow().isTerminal():ars.isTerminal();
    }

}
