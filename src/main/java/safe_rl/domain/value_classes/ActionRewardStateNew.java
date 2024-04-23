package safe_rl.domain.value_classes;

import lombok.Builder;
import lombok.NonNull;
import safe_rl.domain.abstract_classes.Action;
import safe_rl.domain.abstract_classes.StateI;
import common.other.NumberFormatterUtil;

@Builder
public record ActionRewardStateNew<V> (
        @NonNull Action action,
        @NonNull Double reward,
         StateI<V> stateNext,
     //   @NonNull Double probAction,
    //    @NonNull Boolean isFail
        @NonNull Boolean isTerminal)
{

    public static <V> ActionRewardStateNew<V> ofAction(Action action) {
        return new ActionRewardStateNew<>(action, 0.0, null, false);
    }


    @Override
    public String toString() {
        var f=NumberFormatterUtil.formatterTwoDigits;
       return "[a = "+ action+
               ", r = "+ f.format(reward)+
               ", sNext = "+ stateNext+
               ", isTerm. = "+ isTerminal+"]";
    }


}
