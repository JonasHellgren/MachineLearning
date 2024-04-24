package safe_rl.environments.trading_electricity;

import com.google.common.base.Preconditions;
import lombok.Builder;
import lombok.With;

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
) {

    public static SettingsTrading new5HoursIncreasingPrice() {
        var settings = SettingsTrading.builder()
                .dt(1)
                .energyBatt(10).powerBattMax(3).priceBattery(10e3)
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

    public void check() {
        Preconditions.checkArgument(powerAvgFcrExtreme()<powerBattMax(),
                "powerFcrExtreme is to large, decrease e.g. powerCapacityFcr");
        Preconditions.checkArgument(priceTraj.length>0,"Empty price trajectory");
    }

    //time for final price item in priceTraj
    public double timeEnd() {
        return (priceTraj.length-1)*dt;
    }

    public double timeTerminal() {
        return priceTraj.length*dt;
    }


    public double powerAvgFcrExtreme() {
        return powerCapacityFcr() * 2 * stdActivationFCR();
    }

    public double powerFcrAvg(double aFcrLumped) {
        return powerCapacityFcr() * aFcrLumped;
    }


}
