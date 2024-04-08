package policy_gradient_problems.environments.maze;

import lombok.Builder;

@Builder
public record MazeSetting(
        int gridWidth, int gridHeight
) {


    public MazeSetting newDefault() {
        return MazeSetting.builder()
                .gridHeight(3).gridWidth(4)
                .build();
    }


}
