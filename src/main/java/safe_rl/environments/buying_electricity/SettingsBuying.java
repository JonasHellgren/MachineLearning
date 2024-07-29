package safe_rl.environments.buying_electricity;

import com.google.common.base.Preconditions;
import lombok.Builder;
import lombok.With;
import safe_rl.domain.environment.interfaces.SettingsEnvironmentI;

@Builder
public record SettingsBuying  (
        double dt,
        double energyBatt,
        double powerBattMax,
        double socMax,
        double priceEnd,
        @With double[] priceTraj
) implements SettingsEnvironmentI
{

    public static SettingsBuying new5HoursIncreasingPrice() {
        return SettingsBuying.builder()
                .dt(1)
                .energyBatt(10).powerBattMax(3).socMax(1)
                .priceEnd(10).priceTraj(new double[]{1,2,3,4,5})
                .build();
    }

    public static SettingsBuying new3HoursSamePrice() {
        return new5HoursIncreasingPrice().withPriceTraj(new double[]{1,1,1});
            }

    public static SettingsBuying new5HoursDecreasingPrice() {
        return new5HoursIncreasingPrice().withPriceTraj(new double[]{5,4,3,2,1});
    }

    //time for final price item in energyPriceTraj
    public double timeEnd() {
        Preconditions.checkArgument(priceTraj.length>0,"Empty price trajectory");
        return (priceTraj.length-1)*dt;
    }

}
