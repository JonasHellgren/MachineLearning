package safe_rl.environments.trading_electricity;

import common.other.Conditionals;
import common.other.NormDistributionSampler;
import lombok.extern.java.Log;
import safe_rl.domain.abstract_classes.Action;
import safe_rl.domain.abstract_classes.EnvironmentI;
import safe_rl.domain.abstract_classes.StateI;
import safe_rl.domain.value_classes.StepReturn;
import safe_rl.helpers.PriceInterpolator;

import java.util.Arrays;

import static java.lang.Math.*;

@Log
public class EnvironmentTrading implements EnvironmentI<VariablesTrading> {
    public static final double FAIL_PENALTY = 10;
    public static final int N_CONSTRAINTS = 5;
    SettingsTrading settings;
    PriceInterpolator interpolator;
    NormDistributionSampler sampler;

    public EnvironmentTrading(SettingsTrading settings) {
        this.settings = settings;
        this.interpolator=new PriceInterpolator(settings.dt(), settings.priceTraj());
        this.sampler=new NormDistributionSampler();
    }

    @Override
    public StepReturn<VariablesTrading> step(StateI<VariablesTrading> state0, Action action) {
        StateTrading s0=(StateTrading) state0;
        var s=settings;
        double dt=s.dt();
        boolean isTerminal=s0.time()+dt>s.timeEnd();
        double power=action.asDouble();
        double aFcrLumped=sampler.sampleFromNormDistribution(0, s.stdActivationFCR());
        double powerFcrAvg= s.powerFcrAvg(aFcrLumped);
        double dSoc=(power+powerFcrAvg)*dt/s.energyBatt();
        double dSoh=-abs(power*dt)/(s.energyBatt()*s.nCyclesLifetime());
        var stateNew=s0.copyWithDtimeDsocDsoh(dt,dSoc,dSoh);
        double reward = calculateReward(s0, power, dSoh);
        double[] constraints=getConstraints(power,s0, stateNew);
        boolean isFail= Arrays.stream(constraints).anyMatch(c -> c>0);
        reward+=(isFail)?-FAIL_PENALTY :0;
        logIfFail(constraints, isFail);

        return StepReturn.<VariablesTrading>builder()
                .state(stateNew)
                .reward(reward)
                .isFail(isFail)
                .isTerminal(isTerminal)
                .build();
    }

    private double calculateReward(StateTrading s0, double power, double dSoh) {
        var s=settings;
        double energySell=max(0, -power * s.dt());  //pos if power is neg (selling)
        double energyBuy=max(0, power * s.dt());  //pos if power is positive (buying)
        double priceEnergy=interpolator.priceAtTime(s0.time());
        return s.priceFCR()* s.powerCapacityFcr()+priceEnergy*energySell-
                (priceEnergy*energyBuy+ s.priceBattery()* Math.abs(dSoh));
    }


    private static void logIfFail(double[] constraints, boolean isFail) {
        Conditionals.executeIfTrue(isFail, () ->
                log.info("Failed step, constraints="+ Arrays.toString(constraints)));
    }

    /**
     *    x<xMax <=> x-xMax<0;  x>xMin <=> -(x-xMin)<0 <=> (xMin-x)<0
     */
    private double[] getConstraints(double power, StateTrading s0, StateI<VariablesTrading> stateNew) {
        StateTrading s1=(StateTrading) stateNew;
        var s=settings;
        double powerFcr= s.powerAvgFcrExtreme();
        double[] c=new double[N_CONSTRAINTS];
        double g=s.gFunction();
        c[0]=(-s.powerBattMax())-(power-powerFcr);   //power-powerFcr>-powerBattMax
        c[1]=(power+powerFcr)-s.powerBattMax();   //power+powerFcr<powerBattMax
        c[2]=s.socMin()-(s0.soc()+g*(power-powerFcr));   //soc0+-->socMin
        c[3]=(s0.soc()+g*(power+powerFcr))-s.socMax();   //soc0+..<socMax
        c[4]=s.socTerminalMin()-(s0.soc()+g*(power-powerFcr)+s.dSocMax(s1.time()));
        //soc+g*(...)+dSocMax>socTerminalMin
        return c;
    }



}
