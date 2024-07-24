package domain_design_tabular_q_learning.domain.trainer.value_objects;

import lombok.Builder;
import lombok.With;

@Builder
public record TrainerProperties(
        @With int nEpisodes
) {

    public static TrainerProperties roadObstacle() {
        return TrainerProperties.builder()
                .nEpisodes(1500)
                .build();
    }


}
