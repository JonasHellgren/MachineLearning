package safe_rl.domain.value_classes;

import lombok.Builder;
import lombok.NonNull;
import safe_rl.domain.abstract_classes.Action;
import safe_rl.domain.abstract_classes.StateI;

@Builder
public record ExperienceMultiStep<V>(
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

 public static <V> ExperienceMultiStep<V> of(@NonNull StateI<V> state,
                                             @NonNull Action action,
                                             @NonNull Double sumRewards,
                                             StateI<V> stateFut,
                                             @NonNull Boolean isStateFutureTerminalOrNotPresent) {
     return ExperienceMultiStep.<V>builder()
             .state(state)
             .actionApplied(action)
             .sumRewards(sumRewards)
             .stateFuture(stateFut)
             .isStateFutureTerminalOrNotPresent(isStateFutureTerminalOrNotPresent)
             .build();
 }


}
