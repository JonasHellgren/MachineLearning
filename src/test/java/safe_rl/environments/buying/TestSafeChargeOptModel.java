package safe_rl.environments.buying;

import common.list_arrays.ListUtils;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvSource;
import safe_rl.environments.buying_electricity.BuySettings;
import safe_rl.environments.buying_electricity.SafeChargeOptModel;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class TestSafeChargeOptModel {

    public static final int SOC_VIOL_INDEX = 2;
    public static final double TOL_POWER = 1e-1;
    SafeChargeOptModel model;

    @BeforeEach
    void init() {
    model= getModelWithSocAndProposedPower(0.5, 1d);
    }

    @Test
    void whenZeroPower_thenNoViolation() {
        Assertions.assertFalse(model.isAnyViolation(0));
    }

    @Test
    void whenNegPower_thenViolation() {
        Assertions.assertTrue(model.isAnyViolation(-1));
    }

    @Test
    void whenHighPower_thenViolation() {
        Assertions.assertTrue(model.isAnyViolation(4));
    }

    @Test
    void givenHighSoC_whenPower1_thenViolation() {
        model= getModelWithSocAndProposedPower(0.95, 1d);
        List<Double> list = model.getConstraintValues(2);
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
            model.correctedPower();
            assertNotEquals(powerProposed,model.correctedPower(), TOL_POWER);
            assertEquals(powerCorrected,model.correctedPower(), TOL_POWER);
        }
    }


    private double setModel(ArgumentsAccessor arguments) {
        double soc= arguments.getDouble(0);
        double powerProposed= arguments.getDouble(1);
        model.setSoCAndPowerProposed(soc,powerProposed);
        return powerProposed;
    }

    private static double getMaxConstraint(List<Double> list) {
        return ListUtils.findMax(list).orElseThrow();
    }


    private static SafeChargeOptModel getModelWithSocAndProposedPower(double soc, double powerProposed) {
        return SafeChargeOptModel.builder()
                .powerProposed(powerProposed).powerMax(3d)
                .settings(BuySettings.new5HoursIncreasingPrice())
                .soc(soc)
                .build();
    }



}
