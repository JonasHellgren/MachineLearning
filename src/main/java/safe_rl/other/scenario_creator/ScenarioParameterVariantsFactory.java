package safe_rl.other.scenario_creator;

import java.util.Set;
import static safe_rl.persistance.ElDataFinals.*;

public class ScenarioParameterVariantsFactory {

    private ScenarioParameterVariantsFactory() {
    }

    public  static ScenarioParameterVariants create() {
        return ScenarioParameterVariants.builder()
                .priceBatterySet(Set.of(PRICE_BATTERY,PRICE_BATTERY2))
                .powerChargeMaxSet(Set.of(PRICE_HW,PRICE_HW2))
                .socStartSet(Set.of(SOC_START,SOC_START2))
                .priceHWAddOnSet(Set.of(PRICE_HW,PRICE_HW2))
                .powerChargeMaxSet(Set.of(POWER_CHARGE_MAX,POWER_CHARGE_MAX2))
                .dayIdxSet(Set.of(0,1))
                .build();
    }

}
