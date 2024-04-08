package policy_gradient_problems.environments.maze;

import lombok.Builder;
import lombok.With;

@Builder
public record MazeSetting(
        int gridWidth, int gridHeight,
        @With double probabilityIntendedDirection
) {


    public static MazeSetting newDefault() {
        return MazeSetting.builder()
                .gridHeight(3).gridWidth(4)
                .probabilityIntendedDirection(0.8)
                .build();
    }


}
