package multi_step_temp_diff.domain.agents.fork;

import lombok.Builder;
import multi_step_temp_diff.domain.agent_abstract.AgentSettingsInterface;

import java.util.HashMap;
import java.util.Map;

@Builder
public record AgentForkTabularSettings(
        Double discountFactor,
        Map<Integer, Double> memory,
        double valueNotPresent,
        int startState) implements AgentSettingsInterface {

    public static final int VALUE_NOT_PRESENT = 0;
    public static final int START_STATE = 0;
    public static final HashMap<Integer, Double> MEMORY = new HashMap<>();

    public static AgentForkTabularSettings getDefault() {
        return AgentForkTabularSettings.builder()
                .discountFactor(1d)
                .memory(MEMORY)
                .valueNotPresent(VALUE_NOT_PRESENT)
                .startState(START_STATE)
                .build();
    }


}
