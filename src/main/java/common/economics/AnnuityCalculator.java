package common.economics;

import common.math.MathUtils;
import lombok.Builder;
import lombok.NonNull;

/**
 * The conversion between price (Є) and yearly cost (Є/year) is done by an annuity calculation
 * Useful for capital cost analysis
 *
 * Simple example: buying an item for 1000 Euro, using it for ten years, zero interest and rest value
 * Then the annuity is 100 Euro/year. Annuity increases by interest.
 * So 10% interest gives here 162 Euro/year.
 *
 * The calculation is based on price of the investment,  rest value, i=interest rate (-) and
 * lifeTimeInYears=the economic lifetime (years).
 *
 */

@Builder
public class AnnuityCalculator {

    @NonNull
    private final Double price, restValue, i, lifeTimeInYears;

    public double annuity() {

        if (MathUtils.isZero(i)) {
            return (price - restValue) / lifeTimeInYears;
        }

        double t1 =price - restValue / pow(i, lifeTimeInYears);
        double t2 = i / (1 - pow(i, -lifeTimeInYears));
        return t1 * t2;
    }

    private double pow(double i, double n) {
        return Math.pow(1 + i, n);
    }


}
