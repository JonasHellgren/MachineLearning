package safe_rl.domain.value_classes;

import lombok.Builder;
import lombok.NonNull;
import safe_rl.domain.value_classes.*;
import safe_rl.domain.abstract_classes.*;


/***
 * Relevant in safe RL
 */

@Builder
public record ExperienceSafe<V> (
        @NonNull Action action,
        @NonNull Double reward,
        @NonNull  StateI<V> stateNext,
        @NonNull Double probAction,
        @NonNull Boolean isTerminal
)

{

    public ExperienceSafe<V> copy() {
        return ExperienceSafe.<V>builder()
                .action(action).reward(reward).stateNext(stateNext).probAction(probAction).isTerminal(isTerminal)
                .build();
    }

}
