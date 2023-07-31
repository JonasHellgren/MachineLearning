package multi_step_td.maze;

import multi_step_temp_diff.domain.agent_valueobj.AgentMazeTabularSettings;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestAgentMazeTabularDefaultSettings {

    @Test
    public void givenDefaultAgentValues_thenExcected() {
        var settings= AgentMazeTabularSettings.getDefault();

        Assertions.assertEquals(1,settings.discountFactor());
        Assertions.assertEquals(0,settings.valueNotPresent());

    }

}
