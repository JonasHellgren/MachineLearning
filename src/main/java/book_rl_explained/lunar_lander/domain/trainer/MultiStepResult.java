package book_rl_explained.lunar_lander.domain.trainer;

import book_rl_explained.lunar_lander.domain.environment.StateLunar;
import lombok.Builder;

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

}
