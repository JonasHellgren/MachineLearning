package multi_step_temp_diff.domain.helpers_common;

import lombok.Builder;

@Builder
public record AgentEvaluatorResults(
        boolean endedInFailState,
        int nofSteps,
        double sumRewards
) {
}
