package safe_rl.environments.factories;

import com.beust.jcommander.internal.Lists;
import common.list_arrays.ArrayUtil;
import common.list_arrays.ListUtils;
import lombok.NoArgsConstructor;
import safe_rl.environments.trading_electricity.SettingsTrading;

import java.util.List;

@NoArgsConstructor
public class SettingsTradingFactory {

    public static SettingsTrading new5HoursIncreasingPrice() {
        var settings = SettingsTrading.builder()
                .dt(1)
                .energyBatt(10).powerBattMax(3).priceBattery(5e3)
                .socMin(0.0).socMax(1).socTerminalMin(0.5)
                .energyPriceTraj(new double[]{0.1, 0.2, 0.3, 0.4, 0.5})
                .capacityPriceTraj(ArrayUtil.createArrayWithSameDoubleNumber(5,0.11))
                //.priceFCR(1)
                .stdActivationFCR(0.1).powerCapacityFcr(1)
                .nCyclesLifetime(5000)
                .build();
        settings.check();
        return settings;
    }

    public static SettingsTrading new3HoursSamePrice() {
        var settings = new5HoursIncreasingPrice().withEnergyPriceTraj(new double[]{1, 1, 1});
        settings.check();
        return settings;
    }

    public static SettingsTrading new5HoursDecreasingPrice() {
        var settings = new5HoursIncreasingPrice()
                .withEnergyPriceTraj(new double[]{0.5, 0.4, 0.3, 0.2, 0.1});
        settings.check();
        return settings;
    }




    public static SettingsTrading new24HoursIncreasingPrice() {
        var settings = SettingsTrading.builder()
                .dt(1)
                .energyBatt(100).powerBattMax(100).priceBattery(30e3)
                .socMin(0.0).socMax(1).socTerminalMin(0.5)
                .energyPriceTraj(ListUtils.toArray(ListUtils.doublesStartStepNitems(0.1, 0.1, 24)))
                .capacityPriceTraj(ArrayUtil.createArrayWithSameDoubleNumber(24,0.03))
                //.priceFCR(0.03)
                .stdActivationFCR(0.1).powerCapacityFcr(1)
                .nCyclesLifetime(2000)
                .build();
        settings.check();
        return settings;
    }

    public static SettingsTrading new24HoursZigSawPrice() {
        List<Double> list = ListUtils.doublesStartStepNitems(0.1, 0.1, 12);
        List<Double> listZigSaw= Lists.newArrayList();
        listZigSaw.addAll(list);
        listZigSaw.addAll(list);
        double[] priceTraj = ListUtils.toArray(listZigSaw);
        var settings = new24HoursIncreasingPrice().withEnergyPriceTraj(priceTraj);
        settings.check();
        return settings;
    }

    public static SettingsTrading new15Hours1Jan2024() {
        double[] priceTraj = new double[]{64.9900, 61.7400, 55.0700, 48.0100, 44.0100, 45.2000, 38.0000, 38.0000,
                33.5600, 32.0300, 32.2900, 36.7000, 41.7200, 49.1800, 58.7500};
        var settings = new24HoursIncreasingPrice().withEnergyPriceTraj(perMWh2PerKWh(priceTraj));
        settings.check();
        return settings;
    }

    public static SettingsTrading new15Hours5Jan2024() {
        double[] priceTraj = new double[]{    526.2500, 349.9500, 181.9100, 149.6000, 112.8200, 100.1000, 92.8700, 84.8800,
                83.2000, 82.0300, 82.1800, 82.5300, 84.1300, 84.4800, 87.4300};
        var settings = new24HoursIncreasingPrice().withEnergyPriceTraj(perMWh2PerKWh(priceTraj));
        settings.check();
        return settings;
    }

    private static double[] perMWh2PerKWh(double[] priceTraj) {
        return ArrayUtil.multWithValue(priceTraj, 1 / 1000d);
    }


}
