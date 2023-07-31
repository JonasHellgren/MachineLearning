package multi_step_temp_diff.domain.agent_valueobj;

import lombok.Builder;
import multi_step_temp_diff.domain.environments.maze.MazeState;

import java.util.HashMap;
import java.util.Map;

@Builder
public record AgentMazeTabularSettings(
        double discountFactor,
        Map<MazeState, Double> memory,
        double valueNotPresent,
        int startX,
        int startY
) {

    public static AgentMazeTabularSettings getDefault() {
        return AgentMazeTabularSettings.builder()
                .discountFactor(1)
                .memory(new HashMap<>())
                .valueNotPresent(0)
                .startX(0).startY(0)
                .build();
    }



}
