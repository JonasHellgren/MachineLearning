package maze_domain_design.domain.trainer.value_objects;

import lombok.Builder;
import lombok.With;
import org.apache.commons.math3.util.Pair;

@Builder
public record TrainerProperties(
        @With int nEpisodes
/*        Pair<Integer, Integer> startXMinMax,
        Pair<Integer, Integer> startYMinMax*/
) {

    public static TrainerProperties roadMaze() {
        return TrainerProperties.builder()
                .nEpisodes(1500)
/*
                .startXMinMax(Pair.create(0, 0))
                .startYMinMax(Pair.create(0, 1))
*/
                .build();
    }


}
