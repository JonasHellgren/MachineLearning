package safe_rl.other.scenario_creator;

import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
public class ScenariosCreator {

    ScenarioParameterVariants parameterVariants;

    public  Set<ScenarioParameters> scenarios() {
        var listOfSets= Sets.cartesianProduct(parameterVariants.listOfSets() );
        Set<ScenarioParameters> spSet=new HashSet<>();
        listOfSets.forEach(s -> spSet.add(ScenarioParameters.builder()
                        .priceBattery((Double) s.get(0))
                        .priceHWAddOn((Double) s.get(1))
                        .socStart((Double) s.get(2))
                        .powerChargeMax((Double) s.get(3))
                        .dayIdx((Integer) s.get(4))
                .build()));
        return spSet;
    }


}
