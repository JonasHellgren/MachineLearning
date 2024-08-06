package safe_rl.environments.trading_electricity;

import safe_rl.domain.environment.aggregates.StateI;
import safe_rl.domain.environment.helpers.PriceInterpolator;

import java.util.Arrays;

import static java.lang.Math.max;

public class RewardAndConstraintEvaluator {
    public static final int N_CONSTRAINTS = 5;

    SettingsTrading settings;
    PriceInterpolator energyPriceInterpolator;
    PriceInterpolator capacityPriceInterpolator;


    public RewardAndConstraintEvaluator(SettingsTrading settings) {
        this.settings = settings;
        this.energyPriceInterpolator =new PriceInterpolator(settings.dt(), settings.energyPriceTraj());
        this.capacityPriceInterpolator =new PriceInterpolator(settings.dt(), settings.capacityPriceTraj());
    }

    public double calculateReward(StateTrading s0, double power, double dSoh) {
        var s=settings;
        double energySell=max(0, -power * s.dt());  //pos if power is neg (selling)
        double energyBuy=max(0, power * s.dt());  //pos if power is positive (buying)
        double priceEnergy= energyPriceInterpolator.priceAtTime(s0.time());
        double priceCap= capacityPriceInterpolator.priceAtTime(s0.time());
        return priceCap*s.powerCapacityFcr(s0.soc())+priceEnergy*energySell-
                (priceEnergy*energyBuy+ s.priceBattery()* Math.abs(dSoh));
    }


    /**
     *    x<xMax <=> x-xMax<0;  x>xMin <=> -(x-xMin)<0 <=> (xMin-x)<0
     */

    public double[] evaluateConstraints(StateTrading s0, double power, StateI<VariablesTrading> stateNew) {
        StateTrading s1=(StateTrading) stateNew;
        var s=settings;
        double powerFcr= s.powerAvgFcrExtreme(s0.soc());
        double PCfcr=s.powerCapacityFcr(s0.soc());
      //  System.out.println("soC="+s0.soc()+", powerFcr = " + powerFcr+", power="+power);
        double dSoCPC = s.dSoCPC(s0.soc());
        double[] c=new double[N_CONSTRAINTS];
        double g=s.gFunction();
/*
        c[0]=-1; //(-s.powerChargeMax())-(power-powerFcr);   //power-powerFcr>-powerChargeMax
        c[1]=-1; // (power+powerFcr)-s.powerChargeMax();   //power+powerFcr<powerChargeMax
*/


        c[0]=(-s.powerChargeMax())-(power-PCfcr);   //power-PCfcr>-powerChargeMax
        c[1]=(power+PCfcr)-s.powerChargeMax();   //power+PCfcr<powerChargeMax

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
