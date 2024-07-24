package domain_design_tabular_q_learning.domain.trainer.value_objects;

import lombok.Builder;

@Builder
public record EpisodeInfoForRecording(
        double pRandomAction,
        double tdErrorAvg
) {
}
