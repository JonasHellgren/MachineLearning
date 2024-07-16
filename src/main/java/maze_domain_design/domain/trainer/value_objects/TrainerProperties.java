package maze_domain_design.domain.trainer.value_objects;

import lombok.Builder;
import org.apache.commons.math3.util.Pair;

@Builder
public record TrainerProperties(
        int nEpisodes,
        Pair<Integer, Integer> startXMinMax,
        Pair<Integer, Integer> startYMinMax,
        Pair<Double, Double> probRandomAction
) {

    public static TrainerProperties roadMaze() {
        return TrainerProperties.builder()
                .nEpisodes(1000)
                .startXMinMax(Pair.create(0, 0))
                .startYMinMax(Pair.create(0, 1))
                .probRandomAction(Pair.create(0.1, 1d))
                .build();
    }

    public double probRandomAction(double progress) {
        Double p0 = probRandomAction.getFirst();
        Double p1 = probRandomAction.getSecond();
        return p0 +progress*(p1-p0);
    }
}
