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
        double powerChargeMax,  //kW
        @With double priceBattery,  //Euro
        Range<Double> socRange,
        @With double socTerminalMin,
        @With double[] energyPriceTraj,  //Euro/kWh
        @With double[] capacityPriceTraj,  //Euro/kW
        @With double stdActivationFCR,
        @With Range<Double> powerCapacityFcrRange,
        @With double nCyclesLifetime,
        @With double failPenalty
) implements SettingsEnvironmentI {


    public void check() {
        double powerCapExt=powerAvgExtremeFromPowerCapacity(powerCapacityFcrRange.upperEndpoint());
        Preconditions.checkArgument(powerAvgFcrExtreme(powerCapExt) < powerChargeMax(),
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

    public double powerCapacityFcr(double soC) {
        double nsr=Math.min(Math.abs(socMax()-soC),Math.abs(socMin()-soC))/normalizedSoCReserve();
        return capacityFcrMax()*nsr+capacityFcrMin() *(1-nsr);
    }

    Double socMax() {
        return socRange.upperEndpoint();
    }

    double socMin() {
        return  socRange.lowerEndpoint();
    }

    private double normalizedSoCReserve() {
        return Math.abs(socMax()-socMin())/2;
    }

    public double powerAvgFcrExtreme(double soC) {
        return powerAvgExtremeFromPowerCapacity(powerCapacityFcr(soC));
    }

    private double powerAvgExtremeFromPowerCapacity(double powerCap) {
        return powerCap * 2 * stdActivationFCR();
    }

    public double powerFcrAvg(double aFcrLumped,double soC) {
        return powerCapacityFcr(soC) * aFcrLumped;
    }

    public double gFunction() {
        return dt / energyBatt;
    }

    /**
     *  The estimated max possible change in SoC for charging capacity is set in a conservative manner
     *  Assumes capacity reduction is due to the maximum FCR capacity
     */

    public double dSocMax(double time,double soC) {
        double capacityReduction = capacityFcrMax();
        double powerLosses = powerAvgExtremeFromPowerCapacity(capacityReduction);
        double dEnergyMax = (timeTerminal() - time) * (powerChargeMax - capacityReduction - powerLosses);
        return dEnergyMax / energyBatt;
    }

    public int nTimeSteps() {
        return capacityPriceTraj.length;
    }

   public double revFCRPerTimeStep(double soC) {
        double priceFCR= Arrays.stream(capacityPriceTraj()).average().orElseThrow();
       return priceFCR * powerCapacityFcr(soC);
    }

    public double dSoCPC(double soC) {
        return powerCapacityFcr(soC)* dt/ energyBatt;
    }

    private Double capacityFcrMin() {
        return powerCapacityFcrRange.lowerEndpoint();
    }

    private Double capacityFcrMax() {
        return powerCapacityFcrRange.upperEndpoint();
    }

}
