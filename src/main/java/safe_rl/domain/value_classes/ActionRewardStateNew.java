package safe_rl.domain.value_classes;

import lombok.NonNull;
import safe_rl.domain.abstract_classes.Action;
import safe_rl.domain.abstract_classes.StateI;

public record ActionRewardStateNew<V> (
        @NonNull Action action,
        @NonNull Double reward,
         StateI<V> stateNext,
     //   @NonNull Double probAction,
    //    @NonNull Boolean isFail
        @NonNull Boolean isTerminal)
{


}
