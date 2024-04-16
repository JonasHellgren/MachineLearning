package safe_rl.environments.buying_electricity;

import com.google.common.base.Preconditions;
import lombok.Builder;

import java.util.Arrays;

@Builder
public record BuySettings(
        double dt,
        double energyBatt,
        double powerBattMax,
        double socMax,
        double priceEnd,
        double[] priceTraj
) {

    public static BuySettings new5HoursIncreasingPrice() {
        return BuySettings.builder()
                .dt(1)
                .energyBatt(10)
                .powerBattMax(3)
                .socMax(1)
                .priceEnd(10)
                .priceTraj(new double[]{1,2,3,4,5})
                .build();

    }

    //time for final price item in priceTraj
    public double timeEnd() {
        Preconditions.checkArgument(priceTraj.length>0,"Empty price trajectory");
        return (priceTraj.length-1)*dt;
    }

}
