package multi_agent_rl.domain.value_classes;

import lombok.Builder;
import lombok.NonNull;
import multi_agent_rl.domain.abstract_classes.ActionJoint;
import multi_agent_rl.domain.abstract_classes.StateI;

/***
 * Item in MultiStepResults list
 */

@Builder
public record MultiStepResultItem<V, O>(
        @NonNull StateI<V, O> state,
        ActionJoint action,
        @NonNull Double sumRewards,
        StateI<V, O> stateFuture,
        AgentActions<V, O> agentActions,
        @NonNull Boolean isStateFutureTerminalOrNotPresent,
        Double valueTarget,
        Double advantage
) {

    public double sumOfRewards() {
        return sumRewards;
    }

    public static <V, O> MultiStepResultItem<V, O> of(@NonNull StateI<V, O> state,
                                                      @NonNull ActionJoint action,
                                                      @NonNull Double sumRewards,
                                                      StateI<V, O> stateFut,
                                                      @NonNull Boolean isStateFutureTerminalOrNotPresent) {
        return MultiStepResultItem.<V, O>builder()
                .state(state)
                .action(action)
                .sumRewards(sumRewards)
                .stateFuture(stateFut)
                .isStateFutureTerminalOrNotPresent(isStateFutureTerminalOrNotPresent)
                .build();
    }


}
