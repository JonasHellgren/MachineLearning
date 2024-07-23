package maze_domain_design.domain.trainer.entities;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import maze_domain_design.domain.trainer.aggregates.Episode;
import maze_domain_design.domain.trainer.aggregates.Recorder;
import maze_domain_design.domain.trainer.value_objects.EpisodeInfoForRecording;

@Builder
@Getter
@ToString
public class Recording {
        int id;
        double sumRewards;
        EpisodeInfoForRecording episodeInfoForRecording;
        int nSteps;

        public static Recording ofIdAndEpisode(int id,Episode episode) {
                var infoForRecording = episode.getInfoForRecording();
                return Recording.builder()
                        .id(id)
                        .sumRewards(episode.sumRewards())
                        .episodeInfoForRecording(infoForRecording)
                        .nSteps(episode.size())
                        .build();
        }
}
