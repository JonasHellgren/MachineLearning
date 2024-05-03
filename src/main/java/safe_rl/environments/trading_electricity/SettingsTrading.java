package safe_rl.environments.trading_electricity;

import com.google.common.base.Preconditions;
import common.list_arrays.ListUtils;
import lombok.Builder;
import lombok.With;
import safe_rl.domain.abstract_classes.SettingsI;

import java.util.ArrayList;
import java.util.List;

@Builder
public record SettingsTrading(
        double dt,
        double energyBatt,
        double powerBattMax,
        @With double priceBattery,
        double socMin, double socMax,
        @With double socTerminalMin,
        @With double[] priceTraj,
        @With double stdActivationFCR,
        @With double powerCapacityFcr,
        @With double priceFCR,
        @With double nCyclesLifetime
) implements SettingsI {

    public static SettingsTrading new5HoursIncreasingPrice() {
        var settings = SettingsTrading.builder()
                .dt(1)
                .energyBatt(10).powerBattMax(3).priceBattery(5e3)
                .socMin(0.0).socMax(1).socTerminalMin(0.5)
                .priceTraj(new double[]{0.1, 0.2, 0.3, 0.4, 0.5})
                .stdActivationFCR(0.1).powerCapacityFcr(1).priceFCR(1)
                .nCyclesLifetime(5000)
                .build();
        settings.check();
        return settings;
    }

    public static SettingsTrading new3HoursSamePrice() {
        var settings = new5HoursIncreasingPrice().withPriceTraj(new double[]{1, 1, 1});
        settings.check();
        return settings;
    }

    public static SettingsTrading new5HoursDecreasingPrice() {
        var settings = new5HoursIncreasingPrice()
                .withPriceTraj(new double[]{0.5, 0.4, 0.3, 0.2, 0.1});
        settings.check();
        return settings;
    }


    public static SettingsTrading new24HoursIncreasingPrice() {
        var settings = SettingsTrading.builder()
                .dt(1)
                .energyBatt(100).powerBattMax(100).priceBattery(30e3)
                .socMin(0.0).socMax(1).socTerminalMin(0.5)
                .priceTraj(ListUtils.toArray(ListUtils.doublesStartStepNitems(0.1, 0.1, 24)))
                .stdActivationFCR(0.1).powerCapacityFcr(1).priceFCR(0.03)
                .nCyclesLifetime(2000)
                .build();
        settings.check();
        return settings;
    }

    public static SettingsTrading new24HoursZigSawPrice() {
        List<Double> list = ListUtils.doublesStartStepNitems(0.1, 0.1, 12);
        List<Double> listZigSaw=new ArrayList<>();
        listZigSaw.addAll(list);
        listZigSaw.addAll(list);

        System.out.println("listZigSaw = " + listZigSaw);
        double[] priceTraj = ListUtils.toArray(listZigSaw);
        var settings = new24HoursIncreasingPrice().withPriceTraj(priceTraj);
        settings.check();
        return settings;
    }


    public void check() {
        Preconditions.checkArgument(powerAvgFcrExtreme() < powerBattMax(),
                "powerFcrExtreme is to large, decrease e.g. powerCapacityFcr");
        Preconditions.checkArgument(priceTraj.length > 0, "Empty price trajectory");
    }

    //time for final price item in priceTraj
    public double timeEnd() {
        return (priceTraj.length - 1) * dt;
    }

    public double timeTerminal() {
        return priceTraj.length * dt;
    }


    public double powerAvgFcrExtreme() {
        return powerCapacityFcr() * 2 * stdActivationFCR();
    }

    public double powerFcrAvg(double aFcrLumped) {
        return powerCapacityFcr() * aFcrLumped;
    }

    public double gFunction() {
        return dt / energyBatt;
    }


    public double dSocMax(double time) {
        double dEnergyMax = (timeTerminal() - time) * (powerBattMax - powerAvgFcrExtreme());
        return dEnergyMax / energyBatt;
    }

    public int nTimeSteps() {
        return priceTraj.length;
    }

    public double revFCRPerTimeStep() {
        return priceFCR * powerCapacityFcr;
    }

    public double dSoCPC() {
        return powerCapacityFcr* dt/ energyBatt;
    }

}
