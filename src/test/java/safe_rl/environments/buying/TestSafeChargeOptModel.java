package safe_rl.environments.buying;

import common.list_arrays.ListUtils;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvSource;
import safe_rl.domain.abstract_classes.Action;
import safe_rl.environments.buying_electricity.SettingsBuying;
import safe_rl.environments.buying_electricity.SafeChargeOptModel;
import safe_rl.environments.buying_electricity.StateBuying;
import safe_rl.environments.buying_electricity.VariablesBuying;

import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Draft test shows isAnyViolation is approx 1k faster than correctedPower
 */

public class TestSafeChargeOptModel {

    public static final int SOC_VIOL_INDEX = 2;
    public static final double TOL_POWER = 1e-1;
    public static final double SOC = 0.5;
    SafeChargeOptModel<VariablesBuying> model;

    @BeforeEach
    void init() {
    model= createModel();
    }

    @Test
    void whenZeroPower_thenNoViolation() {
        //model.setSoCAndPowerProposed(SOC,0d);
        model.setModel(StateBuying.newSoc(SOC), Action.ofDouble(0d));

        Assertions.assertFalse(model.isAnyViolation());
    }

    @Test
    void whenNegPower_thenViolation() {
        //model.setSoCAndPowerProposed(SOC,-1d);
        model.setModel(StateBuying.newSoc(SOC), Action.ofDouble(-1d));
        Assertions.assertTrue(model.isAnyViolation());
    }

    @Test
    void whenHighPower_thenViolation() {
        //model.setSoCAndPowerProposed(SOC,4d);
        model.setModel(StateBuying.newSoc(SOC), Action.ofDouble(4d));
        Assertions.assertTrue(model.isAnyViolation());
    }

    @Test
    void givenHighSoC_whenPower1_thenViolation() {
        //model.setSoCAndPowerProposed(0.95,1d);
        model.setModel(StateBuying.newSoc(0.95), Action.ofDouble(1d));
        List<Double> list = model.getConstraintValues();
        Assertions.assertTrue(getMaxConstraint(list) >0);
        Assertions.assertTrue(list.get(SOC_VIOL_INDEX)>0);
    }


    @SneakyThrows
    @ParameterizedTest
    @CsvSource({
            "0.1,0",
            "0.1,2",
            "0.9,0.5",
    })
    void whenSoCAndOkPower_thenNoChangedInCorrected(ArgumentsAccessor arguments) {
        double powerProposed = setModel(arguments);
        assertEquals(powerProposed,model.correctedPower(), TOL_POWER);
    }

    @SneakyThrows
    @ParameterizedTest
    @CsvSource({
            "0.1,-1, 0",
            "0.1,5,  3",
            "0.9999,1.0, 0",
    })
    void whenSoCAndNotOkPower_thenChangedInCorrected(ArgumentsAccessor arguments) {
        double powerProposed = setModel(arguments);
        double powerCorrected= arguments.getDouble(2);
        for (int  i = 0; i < 100 ; i++) {
            double correctedPower=model.correctedPower();
            assertNotEquals(powerProposed,correctedPower, TOL_POWER);
            assertEquals(powerCorrected,correctedPower, TOL_POWER);
        }
    }


    private double setModel(ArgumentsAccessor arguments) {
        double soc= arguments.getDouble(0);
        double powerProposed= arguments.getDouble(1);
        //model.setSoCAndPowerProposed(soc,powerProposed);
        model.setModel(StateBuying.newSoc(soc), Action.ofDouble(powerProposed));

        return powerProposed;
    }

    private static double getMaxConstraint(List<Double> list) {
        return ListUtils.findMax(list).orElseThrow();
    }

    private static SafeChargeOptModel<VariablesBuying> createModel() {
        return SafeChargeOptModel.<VariablesBuying>builder()
                .powerProposed(0d).powerMax(3d)
                .settings(SettingsBuying.new5HoursIncreasingPrice())
                .soc(SOC)
                .build();
    }



}
