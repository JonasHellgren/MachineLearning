package multi_step_td.charge;

import multi_step_temp_diff.domain.environment_valueobj.ChargeEnvironmentSettings;
import multi_step_temp_diff.domain.environments.charge.ChargeEnvironmentLambdas;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static multi_step_temp_diff.domain.environments.charge.ChargeEnvironmentLambdas.*;

public class TestChargeEnvironmentLambdas {


    ChargeEnvironmentLambdas lambdas;

    @BeforeEach
    public void init() {
        ChargeEnvironmentSettings settings=ChargeEnvironmentSettings.newDefault();
        lambdas=new ChargeEnvironmentLambdas(settings);
    }

    @Test
    public void whenPosIs20_thenAtChargePos() {
        Assertions.assertTrue(lambdas.isAtChargeQuePos.test(20));  //using settings -> needs constructed object
    }

    @Test
    public void whenPosDiffers_thenMoving() {
        Assertions.assertTrue(isMoving.test(0,1)); //not using settings -> can use static method
    }


}
