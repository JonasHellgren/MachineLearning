package maze_domain_design.domain.trainer.value_objects;

import lombok.Builder;

@Builder
public record EpisodeInfoForRecording(
        double pRandomAction,
        double tdErrorAvg
) {
}
