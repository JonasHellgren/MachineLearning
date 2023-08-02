package multi_step_temp_diff.domain.environments.charge;

import multi_step_temp_diff.domain.environment_valueobj.ChargeEnvironmentSettings;

import java.util.function.Predicate;

public class ChargeEnvironmentFunctionsAndPredicates {

    ChargeEnvironmentSettings settings;

    public ChargeEnvironmentFunctionsAndPredicates(ChargeEnvironmentSettings settings) {
        this.settings = settings;
    }

    public Predicate<Integer> isAtChargeQuePos = (p) -> p == settings.chargeQuePos();

}
