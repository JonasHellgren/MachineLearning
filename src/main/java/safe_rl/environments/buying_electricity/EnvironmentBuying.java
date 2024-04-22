package safe_rl.environments.buying_electricity;

import common.other.Conditionals;
import lombok.extern.java.Log;
import safe_rl.domain.abstract_classes.EnvironmentI;
import safe_rl.domain.abstract_classes.*;
import safe_rl.domain.value_classes.StepReturn;
import safe_rl.helpers.PriceInterpolator;
import java.util.Arrays;
import static java.lang.Math.min;

@Log
public class EnvironmentBuying  implements EnvironmentI<VariablesBuying> {
    public static final double FAIL_PENALTY = 10;
    BuySettings settings;
    PriceInterpolator interpolator;

    public EnvironmentBuying(BuySettings settings) {
        this.settings = settings;
        this.interpolator=new PriceInterpolator(settings.dt(),settings.priceTraj());
    }

    @Override
    public StepReturn<VariablesBuying> step(StateI<VariablesBuying> state0, Action action) {
        StateBuying s0=(StateBuying) state0;
        double dt=settings.dt();
        double timeNew=s0.time()+dt;
        boolean isTerminal=timeNew>settings.timeEnd();
        double power=(isTerminal)?0:action.asDouble();
        double socNew=(isTerminal)?s0.socStart():s0.soc()+power*dt/settings.energyBatt();
        var stateNew=new StateBuying(new VariablesBuying(timeNew,socNew,s0.socStart()));
        double priceAtTime=interpolator.priceAtTime(s0.time());
        double reward=(isTerminal)
                ? (s0.soc()-s0.socStart())*settings.energyBatt()*settings.priceEnd()
                : min(0,-power*dt*priceAtTime);  //can't sell energy before end
        double[] constraints=getConstraints(power,socNew);
        boolean isFail= Arrays.stream(constraints).anyMatch(c -> c>0);
        reward+=(isFail)?-FAIL_PENALTY :0;
        logIfFail(constraints, isFail);
        return StepReturn.<VariablesBuying>builder()
                .state(stateNew)
                .reward(reward)
                .isFail(isFail)
                .isTerminal(isTerminal)
                .build();
    }

    private static void logIfFail(double[] constraints, boolean isFail) {
        Conditionals.executeIfTrue(isFail, () ->
            log.info("Failed step, constraints="+ Arrays.toString(constraints)));
    }

    private double[] getConstraints(double power, double socNew) {
        double[] c=new double[3];
        c[0]=-power;   //power>0
        c[1]=power-settings.powerBattMax();   //power<powerBattMax
        c[2]=socNew-settings.socMax();   //soc<socMax
        return c;
    }
}
