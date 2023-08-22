package multi_step_td.charge;

import multi_step_temp_diff.domain.environments.charge.ChargeEnvironmentSettings;
import multi_step_temp_diff.domain.environments.charge.ChargeEnvironmentLambdas;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvSource;

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

    @Test
    public void whenActionIs10_thenNotValid() {
        Assertions.assertTrue(lambdas.isNonValidAction.test(10));
    }

    @ParameterizedTest
    @CsvSource({"-1,true", "60,true", "0,false", "10,false"})
    public void whenPos_thenValidOrNotValid(ArgumentsAccessor arguments) {
        int pos = arguments.getInteger(0);
        boolean expected = arguments.getBoolean(1);
        Assertions.assertEquals(expected,lambdas.isNonValidPos.test(pos));
    }

    @ParameterizedTest
    @CsvSource({"20,20,true", "20,21,false", "10,10,false", "19,20,false"})
    public void whenPosAndPosNew_thenValidOrNotValid(ArgumentsAccessor arguments) {
        int pos = arguments.getInteger(0), posNew=arguments.getInteger(1);
        boolean expected = arguments.getBoolean(2);
        Assertions.assertEquals(expected,lambdas.isStillAtChargeQuePos.test(pos,posNew));
    }

}
