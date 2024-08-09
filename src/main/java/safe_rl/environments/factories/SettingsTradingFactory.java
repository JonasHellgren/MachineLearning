package safe_rl.environments.factories;

import com.beust.jcommander.internal.Lists;
import com.google.common.collect.Range;
import common.list_arrays.ArrayUtil;
import common.list_arrays.ListUtils;
import lombok.NoArgsConstructor;
import org.apache.commons.math3.util.Pair;
import safe_rl.environments.trading_electricity.SettingsTrading;

import java.util.List;

import static safe_rl.persistance.ElDataFinals.POWER_TOL;

@NoArgsConstructor
public class SettingsTradingFactory {

    public static final int FAIL_PENALTY = 10;

    public static final double[] EMPTY_ARR = {};

    public static SettingsTrading new5HoursIncreasingPrice() {
        var settings = SettingsTrading.builder()
                .dt(1)
                .energyBatt(10)  //kWh
                .powerChargeRange(Range.closed(-3d,3d))  //kW
                .powerTolerance(POWER_TOL)
                .priceBattery(5e3)
                .socRange(Range.closed(0d,1d))
                .socTerminalMin(0.5)
                .energyPriceTraj(new double[]{0.1, 0.2, 0.3, 0.4, 0.5})  //Eur/kWh
                .capacityPriceTraj(ArrayUtil.createArrayWithSameDoubleNumber(5,0.11))  //Eur/kW
                .stdActivationFCR(0.1)
                .powerCapacityFcrRange(Range.closed(0d,1d))
                .nCyclesLifetime(5000)
                .failPenalty(FAIL_PENALTY)
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

    public static SettingsTrading new100kWhVehicleEmptyPrices() {
        return SettingsTrading.builder()
                .dt(1)
                .energyBatt(100)
                .powerChargeRange(Range.closed(-22d,22d))
                .powerTolerance(POWER_TOL)
                .priceBattery(30e3)
                .socRange(Range.closed(0d,1d))
                .fromToHour(Pair.create(17, 8))
                .socTerminalMin(0.5)
                .energyPriceTraj(EMPTY_ARR).capacityPriceTraj(EMPTY_ARR)
                .stdActivationFCR(0.1)
                .powerCapacityFcrRange(Range.closed(0d,1d))
                .nCyclesLifetime(2000)
                .failPenalty(FAIL_PENALTY)
                .build();
    }

    public static SettingsTrading getSettingsV2G(Pair<List<Double>,List<Double>> energyFcrPricePair,
                                                 double cap,
                                                 double socTerminalMin,
                                                 double powerChargeMax,
                                                 double priceBattery) {
        return SettingsTradingFactory.new100kWhVehicleEmptyPrices()
                .withPowerCapacityFcrRange(Range.closed(0d, cap))
                .withPowerChargeRange(Range.closed(-powerChargeMax, powerChargeMax))
                .withPriceBattery(priceBattery)
                .withEnergyPriceTraj(ListUtils.toArray(energyFcrPricePair.getFirst()))
                .withCapacityPriceTraj(ListUtils.toArray(energyFcrPricePair.getSecond()))
                .withSocTerminalMin(socTerminalMin);
    }

    public static SettingsTrading getSettingsG2V(Pair<List<Double>,List<Double>> energyFcrPricePair,
                                                 double socTerminalMin,
                                                 double powerChargeMax,
                                                 double priceBattery) {
        return SettingsTradingFactory.new100kWhVehicleEmptyPrices()
                .withPowerCapacityFcrRange(Range.closed(0d, 0d))
                .withPowerChargeRange(Range.closed(0d, powerChargeMax))
                .withPriceBattery(priceBattery)
                .withEnergyPriceTraj(ListUtils.toArray(energyFcrPricePair.getFirst()))
                .withCapacityPriceTraj(ListUtils.toArray(energyFcrPricePair.getSecond()))
                .withSocTerminalMin(socTerminalMin);
    }


    public static SettingsTrading new24HoursIncreasingPrice() {
        var settings = SettingsTrading.builder()
                .dt(1)
                .energyBatt(100)
                .powerChargeRange(Range.closed(-100d,100d))
                .powerTolerance(POWER_TOL)
                .priceBattery(30e3)
                .socRange(Range.closed(0d,1d))
                .socTerminalMin(0.5)
                .energyPriceTraj(ListUtils.toArray(ListUtils.doublesStartStepNitems(0.1, 0.1, 24)))
                .capacityPriceTraj(ArrayUtil.createArrayWithSameDoubleNumber(24,0.03))
                .stdActivationFCR(0.1)
                .powerCapacityFcrRange(Range.closed(0d,1d))
                .nCyclesLifetime(2000)
                .failPenalty(FAIL_PENALTY)
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
