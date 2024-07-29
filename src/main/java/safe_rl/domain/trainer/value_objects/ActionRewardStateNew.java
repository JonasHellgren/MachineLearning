package safe_rl.domain.trainer.value_objects;

import lombok.Builder;
import lombok.NonNull;
import safe_rl.domain.environment.value_objects.Action;
import safe_rl.domain.environment.aggregates.StateI;
import common.other.NumberFormatterUtil;

/**
 * Part of experience
 */

@Builder
public record ActionRewardStateNew<V>(
        @NonNull Action action,
        @NonNull Double reward,
        StateI<V> stateNext,
        @NonNull Boolean isTerminal) {

    public static <V> ActionRewardStateNew<V> ofAction(Action action) {
        return new ActionRewardStateNew<>(action, 0.0, null, false);
    }


    @Override
    public String toString() {
        var f = NumberFormatterUtil.formatterTwoDigits;
        return "[a = " + action +
                ", r = " + f.format(reward) +
                ", sNext = " + stateNext +
                ", isTerm. = " + isTerminal + "]";
    }


}
