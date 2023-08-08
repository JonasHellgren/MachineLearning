package multi_step_temp_diff.domain.agent_valueobj;

import lombok.Builder;
import multi_step_temp_diff.domain.environments.maze.MazeState;

import java.util.HashMap;
import java.util.Map;

@Builder
public record AgentMazeTabularSettings (
        Double discountFactor,
        Map<MazeState, Double> memory,
        double valueNotPresent,
        int startX,
        int startY
) implements AgentSettingsInterface {

    public static  double VALUE_IF_NOT_PRESENT=0;

    public static AgentMazeTabularSettings getDefault() {
        return AgentMazeTabularSettings.builder()
                .discountFactor(1d)
                .memory(new HashMap<>())
                .valueNotPresent(VALUE_IF_NOT_PRESENT)
                .startX(0).startY(0)
                .build();
    }

}
