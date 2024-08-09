package safe_rl.environments.trading_electricity;

import com.google.common.base.Preconditions;
import com.google.common.collect.Range;
import common.math.MathUtils;
import lombok.Builder;
import lombok.With;
import org.apache.commons.math3.util.Pair;
import safe_rl.domain.environment.interfaces.SettingsEnvironmentI;

import java.util.Arrays;

@Builder
public record SettingsTrading(
        double dt,
        double energyBatt,  //kWh
        @With Range<Double> powerChargeRange,  //kW
        double powerTolerance, //kW helps finding start point when powerMin is zero
        @With double priceBattery,  //Euro
        Range<Double> socRange,
        @With  Pair<Integer, Integer> fromToHour,
        @With double socTerminalMin,
        @With double[] energyPriceTraj,  //Euro/kWh
        @With double[] capacityPriceTraj,  //Euro/kW
        @With double stdActivationFCR,
        @With Range<Double> powerCapacityFcrRange,
        @With double nCyclesLifetime,
        @With double failPenalty
) implements SettingsEnvironmentI {

    @Builder
    record DataChecker(
            boolean isPowerCapOk,
            boolean isPowerChargeRangeOk,
            boolean isNotFcrAndZeroPowerChargeMin,
            boolean isEnergyTrajLengthOk,
            boolean isCapacityTrajLengthOk
    ) {
        boolean isOk() {
            return isPowerCapOk && isPowerChargeRangeOk && isNotFcrAndZeroPowerChargeMin &&
                    isEnergyTrajLengthOk && isCapacityTrajLengthOk ;
        }
    }

    public void check() {
        DataChecker checker = getDataChecker();
        Preconditions.checkArgument(checker.isPowerCapOk,
                "powerFcrExtreme is to large, decrease e.g. powerCapacityFcr");
        Preconditions.checkArgument(checker.isPowerChargeRangeOk,"Faulty power range");

        System.out.println("MathUtils.isNonZero(maxPowerCapacityFcr()) = " + MathUtils.isNonZero(maxPowerCapacityFcr()));
        System.out.println("maxPowerCapacityFcr() = " + maxPowerCapacityFcr());
        System.out.println("powerChargeMin() = " + powerChargeMin());
        Preconditions.checkArgument(checker.isNotFcrAndZeroPowerChargeMin,
                "FCR requires V2G (also neg charge power)");
        Preconditions.checkArgument(checker.isEnergyTrajLengthOk, "Empty energy price trajectory");
        Preconditions.checkArgument(checker.isCapacityTrajLengthOk, "Empty cap price trajectory");

    }

    public boolean isDataOk() {
        DataChecker checker = getDataChecker();
        return checker.isOk();
    }

    public double minAbsolutePowerCharge() {
        return Math.min(Math.abs(powerChargeMin()), Math.abs(powerChargeMax()));
    }

    public Double powerChargeMin() {
        return powerChargeRange.lowerEndpoint();
    }

    public Double powerChargeMax() {
        return powerChargeRange.upperEndpoint();
    }

    //time for final price item in energyPriceTraj
    public double timeEnd() {
        return (capacityPriceTraj.length - 1) * dt;
    }

    public double timeTerminal() {
        return capacityPriceTraj.length * dt;
    }

    public double powerCapacityFcr(double soC) {
        double nsr = Math.min(Math.abs(socMax() - soC), Math.abs(socMin() - soC)) / normalizedSoCReserve();
        return capacityFcrMax() * nsr + capacityFcrMin() * (1 - nsr);
    }

    Double socMax() {
        return socRange.upperEndpoint();
    }

    double socMin() {
        return socRange.lowerEndpoint();
    }

    private double normalizedSoCReserve() {
        return Math.abs(socMax() - socMin()) / 2;
    }

    public double powerAvgFcrExtreme(double soC) {
        return powerAvgExtremeFromPowerCapacity(powerCapacityFcr(soC));
    }

    public double powerAvgExtremeFromPowerCapacity(double powerCap) {
        return powerCap * 2 * stdActivationFCR();
    }

    public double powerFcrAvg(double aFcrLumped, double soC) {
        return powerCapacityFcr(soC) * aFcrLumped;
    }

    public double gFunction() {
        return dt / energyBatt;
    }

    /**
     * The estimated max possible change in SoC for charging capacity is set in a conservative manner
     * Assumes capacity reduction is due to the maximum FCR capacity
     */

    public double dSocMax(double time, double soC) {
        double capacityReduction = capacityFcrMax();
        double powerLosses = powerAvgExtremeFromPowerCapacity(capacityReduction);
        double dEnergyMax = (timeTerminal() - time) * (powerChargeMax() - capacityReduction - powerLosses);
        return dEnergyMax / energyBatt;
    }

    public int nTimeSteps() {
        return capacityPriceTraj.length;
    }

    public double revFCRPerTimeStep(double soC) {
        double priceFCR = Arrays.stream(capacityPriceTraj()).average().orElseThrow();
        return priceFCR * powerCapacityFcr(soC);
    }

    public double dSoCPC(double soC) {
        return powerCapacityFcr(soC) * dt / energyBatt;
    }

    private Double capacityFcrMin() {
        return powerCapacityFcrRange.lowerEndpoint();
    }

    private Double capacityFcrMax() {
        return powerCapacityFcrRange.upperEndpoint();
    }

    private DataChecker getDataChecker() {
        boolean isFcr=MathUtils.isNonZero(maxPowerCapacityFcr());
        return DataChecker.builder()
                .isPowerChargeRangeOk(
                        powerChargeMin() < powerChargeMax() &&
                                powerChargeMax() > 0 &&
                                powerChargeMin() < Double.MIN_VALUE
                )
                .isPowerCapOk(powerAvgFcrExtreme(maxPowerCapacityFcr()) < minAbsolutePowerCharge()+Double.MIN_VALUE)
                .isNotFcrAndZeroPowerChargeMin(!(isFcr && MathUtils.isZero(powerChargeMin())))
                .isEnergyTrajLengthOk(energyPriceTraj.length > 0)
                .isCapacityTrajLengthOk(capacityPriceTraj.length > 0)
                .build();
    }


    private double maxPowerCapacityFcr() {
        return powerAvgExtremeFromPowerCapacity(powerCapacityFcrRange.upperEndpoint());
    }
/*

                    .isNotFcrAndZeroPowerChargeMin(MathUtils.isNonZero(maxPowerCapacityFcr())
            ? MathUtils.isNeg(powerChargeMin())
            : MathUtils.isZero(powerChargeMin()))
*/


}
