package safe_rl.domain.value_classes;

import lombok.Builder;
import lombok.NonNull;
import lombok.With;
import safe_rl.domain.value_classes.*;
import safe_rl.domain.abstract_classes.*;

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

    public static final double DEFAULT_VALUE = 0d;
    public static final int PROB_ACTION = 1;

    public static <V> Experience<V> notSafeCorrected(StateI<V> state,
                                                     Action action,
                                                     double reward,
                                                     StateI<V> stateNext,
                                                     Boolean isTerminal
    ) {
        return Experience.<V>builder()
                .state(state)
                .ars(new ActionRewardStateNew<>(action, reward, stateNext, isTerminal))
                .value(0)
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
                .ars(new ActionRewardStateNew<>(action, 0d, null, false))
                .value(0)
                .arsCorrected(Optional.of(new ActionRewardStateNew<>(actionSafeCorrected,reward,stateNext,isTerminal)))
                .build();
    }


    public boolean isSafeCorrected() {
        return arsCorrected().isPresent();
    }

    public Experience<V> copyWithValue(double value) {
        return this.withValue(value);
    }
}