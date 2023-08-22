package multi_step_td.charge;

import multi_step_temp_diff.domain.environments.charge.ChargeEnvironmentSettings;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestChargeEnvironmentSettings {

    ChargeEnvironmentSettings envSettings;

    @Test
    public void whenDefault_thenCorrect() {
        envSettings=ChargeEnvironmentSettings.newDefault();
        assertEquals(4,envSettings.nofActions());
        assertEquals(1000,envSettings.maxNofSteps());
    }

    @Test
    public void whenDefaultCopy_thenCorrect() {
        envSettings=ChargeEnvironmentSettings.newDefault();
        ChargeEnvironmentSettings envSettingsCopy=envSettings.copyWithNewMaxNofSteps(100);
        assertEquals(4,envSettingsCopy.nofActions());
        assertEquals(100,envSettingsCopy.maxNofSteps());
    }

}
