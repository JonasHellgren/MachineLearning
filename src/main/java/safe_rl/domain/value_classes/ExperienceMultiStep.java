package safe_rl.domain.value_classes;

import common.list_arrays.ListUtils;
import lombok.Builder;
import lombok.NonNull;
import safe_rl.domain.abstract_classes.Action;
import safe_rl.domain.abstract_classes.StateI;

import java.util.List;

@Builder
public record ExperienceMultiStep<V>(
        @NonNull StateI<V> state,
        List<Action> actions,
        @NonNull List<Double> rewards,
        StateI<V> stateFuture,
        @NonNull Boolean isStateFutureTerminalOrNotPresent
) {

 public double sumOfRewards () {
     return  ListUtils.sumList(rewards());
 }


}
