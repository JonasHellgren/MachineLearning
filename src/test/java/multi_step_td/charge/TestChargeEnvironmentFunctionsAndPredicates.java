package multi_step_td.charge;

import multi_step_temp_diff.domain.environment_valueobj.ChargeEnvironmentSettings;
import multi_step_temp_diff.domain.environments.charge.ChargeEnvironmentFunctionsAndPredicates;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestChargeEnvironmentFunctionsAndPredicates {


    ChargeEnvironmentFunctionsAndPredicates lambdas;

    @BeforeEach
    public void init() {
        ChargeEnvironmentSettings settings=ChargeEnvironmentSettings.newDefault();
        lambdas=new ChargeEnvironmentFunctionsAndPredicates(settings);
    }

    @Test
    public void when() {
        lambdas.isAtChargeQuePos.test(20);
    }

}
