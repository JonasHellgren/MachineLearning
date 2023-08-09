package multi_step_temp_diff.domain.test_helpers;

import lombok.Builder;
import lombok.ToString;

@Builder
public record AgentEvaluatorResults(
        boolean endedInFailState,
        int nofSteps,
        double sumRewards
) {
}
