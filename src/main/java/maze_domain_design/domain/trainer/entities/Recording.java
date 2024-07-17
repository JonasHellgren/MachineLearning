package maze_domain_design.domain.trainer.entities;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import maze_domain_design.domain.trainer.aggregates.Episode;
import maze_domain_design.domain.trainer.aggregates.Recorder;

@Builder
@Getter
@ToString
public class Recording {
        int id;
        double sumRewards;
        double pRandomAction;
        int nSteps;

        public static Recording ofIdAndEpisode(int id,Episode episode) {
                return Recording.builder()
                        .id(id)
                        .sumRewards(episode.sumRewards())
                        .pRandomAction(episode.getPRandomAction())
                        .nSteps(episode.size())
                        .build();
        }
}
