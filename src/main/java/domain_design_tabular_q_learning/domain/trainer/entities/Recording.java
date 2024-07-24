package domain_design_tabular_q_learning.domain.trainer.entities;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import domain_design_tabular_q_learning.domain.trainer.aggregates.Episode;
import domain_design_tabular_q_learning.domain.trainer.value_objects.EpisodeInfoForRecording;

@Builder
@Getter
@ToString
public class Recording{
        int id;
        double sumRewards;
        EpisodeInfoForRecording episodeInfoForRecording;
        int nSteps;

        public static <V> Recording ofIdAndEpisode(int id,Episode<V> episode) {
                var infoForRecording = episode.getInfoForRecording();
                return Recording.builder()
                        .id(id)
                        .sumRewards(episode.sumRewards())
                        .episodeInfoForRecording(infoForRecording)
                        .nSteps(episode.size())
                        .build();
        }
}
