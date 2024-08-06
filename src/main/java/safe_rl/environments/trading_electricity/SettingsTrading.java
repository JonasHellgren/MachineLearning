package safe_rl.environments.trading_electricity;

import com.google.common.base.Preconditions;
import com.google.common.collect.Range;
import lombok.Builder;
import lombok.With;
import safe_rl.domain.environment.interfaces.SettingsEnvironmentI;
import java.util.Arrays;

@Builder
public record SettingsTrading(
        double dt,
        double energyBatt,  //kWh
        double powerBattMax,  //kW
        @With double priceBattery,  //Euro
        //double socMin, double socMax,
        Range<Double> socRange,
        @With double socTerminalMin,
        @With double[] energyPriceTraj,  //Euro/kWh
        @With double[] capacityPriceTraj,  //Euro/kW
        @With double stdActivationFCR,
        @With double powerCapacityFcr,
        @With double nCyclesLifetime,
        @With double failPenalty
) implements SettingsEnvironmentI {


    public void check() {
        Preconditions.checkArgument(powerAvgFcrExtreme() < powerBattMax(),
                "powerFcrExtreme is to large, decrease e.g. powerCapacityFcr");
        Preconditions.checkArgument(energyPriceTraj.length > 0, "Empty energy price trajectory");
        Preconditions.checkArgument(capacityPriceTraj.length > 0, "Empty cap price trajectory");
    }

    //time for final price item in energyPriceTraj
    public double timeEnd() {
        return (capacityPriceTraj.length - 1) * dt;
    }

    public double timeTerminal() {
        return capacityPriceTraj.length * dt;
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
        return capacityPriceTraj.length;
    }

   public double revFCRPerTimeStep() {
        double priceFCR= Arrays.stream(capacityPriceTraj()).average().orElseThrow();
       return priceFCR * powerCapacityFcr;
    }

    public double dSoCPC() {
        return powerCapacityFcr* dt/ energyBatt;
    }

}
