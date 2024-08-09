package safe_rl.other.scenario_creator;

import lombok.Builder;

@Builder
public record ScenarioParameters(double priceBattery,
                                 double priceHWAddOn,
                                 double socStart,
                                 double powerChargeMax,
                                 int dayIdx
) {

}