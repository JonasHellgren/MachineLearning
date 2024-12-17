package book_rl_explained.lunar_lander.domain.trainer;

import book_rl_explained.lunar_lander.domain.environment.StateLunar;
import lombok.Builder;
import lombok.NonNull;

@Builder
public record MultiStepResult(
        StateLunar state,
        double action,
        Double sumRewards,
        StateLunar stateFuture,
        Boolean isStateFutureTerminalOrNotPresent,
        Double valueTarget,
        Double advantage,
        double tdError
) {
/*
    @NonNull
    public static MultiStepResult core(StateLunar state,
                                       double action,
                                       Double sumRewards,
                                       StateLunar stateFut,
                                       Boolean isStateFutureTerminalOrNotPresent) {
        return MultiStepResult.builder()
                .state(state)
                .action(action)
                .sumRewards(sumRewards)
                .stateFuture(stateFut)
                .isStateFutureTerminalOrNotPresent(isStateFutureTerminalOrNotPresent)
                .build();
    }*/

}
