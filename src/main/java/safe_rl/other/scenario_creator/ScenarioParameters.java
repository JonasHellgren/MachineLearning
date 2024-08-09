package safe_rl.other.scenario_creator;

import lombok.Builder;

import java.util.List;

@Builder
public record ScenarioParameters(double priceBattery,
                                 double priceHWAddOn,
                                 double socStart,
                                 double powerChargeMax,
                                 int dayIdx
) {

    public static List<String> names() {
        return List.of("priceBattery","priceHWAddOn", "socStart", "powerChargeMax","dayIdx");
    }

    public  List<Number> asListOfNumbers() {
        return List.of(priceBattery,priceHWAddOn, socStart, powerChargeMax,dayIdx);
    }


}