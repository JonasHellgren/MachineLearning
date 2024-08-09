package safe_rl.environments.trading;

import com.google.common.collect.Range;
import common.list_arrays.ListUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import safe_rl.environments.factories.SettingsTradingFactory;
import safe_rl.environments.trading_electricity.SettingsTrading;

public class TestSettingsTrading {
    public static final double PRICE_BATTERY = 30e3;
    public static final double STD_ACTIVATION_FCR = 0.1;


    SettingsTrading settings;

    @BeforeEach
    public void inti() {
        settings = SettingsTradingFactory.new24HoursIncreasingPrice()  //case 0 and 1
                .withPowerCapacityFcrRange(Range.closed(0d,10d))
                .withStdActivationFCR(STD_ACTIVATION_FCR)
                .withSocStart(0.5).withSocDelta(0.4)
                .withPriceBattery(PRICE_BATTERY);
    }

    @Test
    void whenPowerCapacityFcr_thenCorrect() {
        double fcrLowSoc=settings.powerCapacityFcr(0.5);
        double fcrHighSoc=settings.powerCapacityFcr(0.9);
        Assertions.assertTrue(fcrHighSoc<fcrLowSoc);

    }


    @Test
    void whenPowerAcgFcrExtreme_thenCorrect() {
        double fcrLowSoc=settings.powerAvgFcrExtreme(0.5);
        double fcrHighSoc=settings.powerAvgFcrExtreme(0.9);
        Assertions.assertTrue(fcrHighSoc<fcrLowSoc);
    }




}
