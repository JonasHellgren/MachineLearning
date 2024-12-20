package safe_rl.domain.trainer.value_objects;

import lombok.Builder;
import lombok.NonNull;
import safe_rl.domain.environment.value_objects.Action;
import safe_rl.domain.environment.aggregates.StateI;

/***
 * Item in MultiStepResults list
 */

@Builder
public record MultiStepResultItem<V>(
        @NonNull StateI<V> state,
        Action actionApplied,
        @NonNull Double sumRewards,
        StateI<V> stateFuture,
        @NonNull Boolean isStateFutureTerminalOrNotPresent,
        Double valueTarget,
        Double advantage,
        Action actionPolicy,
        Boolean isSafeCorrected
) {

 public double sumOfRewards () {
     return  sumRewards;
 }

 public static <V> MultiStepResultItem<V> of(@NonNull StateI<V> state,
                                             @NonNull Action action,
                                             @NonNull Double sumRewards,
                                             StateI<V> stateFut,
                                             @NonNull Boolean isStateFutureTerminalOrNotPresent) {
     return MultiStepResultItem.<V>builder()
             .state(state)
             .actionApplied(action)
             .sumRewards(sumRewards)
             .stateFuture(stateFut)
             .isStateFutureTerminalOrNotPresent(isStateFutureTerminalOrNotPresent)
             .build();
 }


}
