package safe_rl.environments.trading_electricity;

import lombok.Builder;
import safe_rl.domain.environment.aggregates.StateI;
import safe_rl.domain.environment.helpers.PriceInterpolator;

import java.util.Arrays;

import static java.lang.Math.max;

public class RewardAndConstraintEvaluator {
    public static final int N_CONSTRAINTS = 5;

    @Builder
    public record IncomesAndCosts (
            double incomeFcr,
            double incomeEnergy,
            double costEnergy,
            double costDegradation
    )
    {
        public double reward() {
            return incomeFcr+incomeEnergy-(costEnergy+costDegradation);
        }

        public double incomeEnergy() {
            return incomeEnergy-costEnergy;
        }
    }

    SettingsTrading settings;
    PriceInterpolator energyPriceInterpolator;
    PriceInterpolator capacityPriceInterpolator;


    public RewardAndConstraintEvaluator(SettingsTrading settings) {
        this.settings = settings;
        this.energyPriceInterpolator =new PriceInterpolator(settings.dt(), settings.energyPriceTraj());
        this.capacityPriceInterpolator =new PriceInterpolator(settings.dt(), settings.capacityPriceTraj());
    }

    public double calculateReward(StateTrading s0, double power, double dSoh) {
        return calculateIncomes(s0,power,dSoh).reward();
    }

    public IncomesAndCosts calculateIncomes(StateTrading s0, double power, double dSoh) {
        var s=settings;
        double energySell=max(0, -power * s.dt());  //pos if power is neg (selling)
        double energyBuy=max(0, power * s.dt());  //pos if power is positive (buying)
        double priceEnergy= energyPriceInterpolator.priceAtTime(s0.time());
        double priceCap= capacityPriceInterpolator.priceAtTime(s0.time());
        return IncomesAndCosts.builder()
                .incomeFcr(priceCap*s.powerCapacityFcr(s0.soc()))
                .incomeEnergy(priceEnergy*energySell)
                .costEnergy(priceEnergy*energyBuy)
                .costDegradation(s.priceBattery()* Math.abs(dSoh))
                .build();
    }


    /**
     *    x<xMax <=> x-xMax<0;  x>xMin <=> -(x-xMin)<0 <=> (xMin-x)<0
     */

    public double[] evaluateConstraints(StateTrading s0, double power, StateI<VariablesTrading> stateNew) {
        StateTrading s1=(StateTrading) stateNew;
        var s=settings;
        double powerFcr= s.powerAvgFcrExtreme(s0.soc());
        double capFcr=s.powerCapacityFcr(s0.soc());
        double dSoCPC = s.dSoCPC(s0.soc());
        double[] c=new double[N_CONSTRAINTS];
        double g=s.gFunction();
        c[0]=(s.powerChargeMin()-s.powerTolerance())-(power-capFcr);   //power-capFcr>powerChargeMin
        c[1]=(power+capFcr)-s.powerChargeMax();   //power+capFcr<powerChargeMax
        c[2]=s.socMin()-(s0.soc()+g*(power-powerFcr)-dSoCPC);   //soc0+-->socMin
        c[3]=(s0.soc()+g*(power+powerFcr)+dSoCPC)-s.socMax();   //soc0+..<socMax
        c[4]=s.socTerminalMin()-(s0.soc()+g*(power-powerFcr)+s.dSocMax(s1.time(),s0.soc()));
        //soc+g*(...)+dSocMax>socTerminalMin
        return c;
    }


    public  boolean isAnyConstraintViolated(double[] constraints) {
        return Arrays.stream(constraints).anyMatch(c -> c>0);
    }


    public double maybeAddFailPenaltyToReward(double reward, boolean isFail) {
        reward += isFail ?-settings.failPenalty() :0;
        return reward;
    }

}
