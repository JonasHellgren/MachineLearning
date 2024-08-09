package safe_rl.other.scenario_creator;

import lombok.Builder;

import java.util.List;
import java.util.Set;

@Builder
public record ScenarioParameterVariants(Set<Double> priceBatterySet,
                                        Set<Double> priceHWAddOnSet,
                                        Set<Double> socStartSet,
                                        Set<Double> powerChargeMaxSet,
                                        Set<Integer> dayIdxSet
) {

    public List<Set<? extends Number>> listOfSets() {
        return List.of(priceBatterySet, priceHWAddOnSet,socStartSet,powerChargeMaxSet,dayIdxSet);
    }

}
