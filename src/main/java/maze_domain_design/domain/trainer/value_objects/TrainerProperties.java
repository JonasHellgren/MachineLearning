package maze_domain_design.domain.trainer.value_objects;

import org.apache.commons.math3.util.Pair;

public record TrainerProperties(
        int nEpisodes,
        Pair<Integer,Integer> startXMinMax,
        Pair<Integer,Integer> startYMinMax
) {
}
