package book_rl_explained.lunar_lander.domain.environment;

import common.math.MathUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hellgren.utilities.math.MyMathUtils;
import org.hellgren.utilities.unit_converter.MyUnitConverter;
import org.hellgren.utilities.unit_converter.NonSIUnits;
import tec.units.ri.unit.Units;

@Getter
@AllArgsConstructor
public class EnvironmentLunar implements EnvironmentI {

    LunarProperties properties;

    public static EnvironmentLunar createDefault() {
        return new EnvironmentLunar(LunarProperties.defaultProps());
    }

    public static EnvironmentI of(LunarProperties ep) {
        return new EnvironmentLunar(ep);
    }


    @Override
    public StepReturnLunar step(StateLunar state, double action) {
        double speed0 = state.variables.spd();
        double y0 = state.variables.y();
        double acc = calculateAcceleration(action);
        double speed = speed0 + acc * properties.dt();
        double y = y0 + speed * properties.dt();

        var stateNew = StateLunar.of(y, speed);
        boolean isTerminal = isLanded(y) || isToHighPosition(y);
        boolean isFail = isTerminal && isToHighSpeed(speed) || isToHighPosition(y);

        //double rewardFail = isFail ? props.rewardFail() : 0d;

       /* boolean isTerminal = isLanded(y);
        boolean isFail = isTerminal && isToHighSpeed(speed);
*/
        double reward = calculateReward(y,speed, acc, isFail);

        return StepReturnLunar.builder()
                .stateNew(stateNew)
                .isFail(isFail)
                .isTerminal(isTerminal)
                .reward(reward)
                .build();
    }

    private boolean isLanded(double y) {
        return y < properties.ySurface();
    }

    private  boolean isToHighPosition(double y) {
        return y > properties.yMax();
    }


    private double calculateReward( double y,double speed, double acc, boolean isFail) {
        double devSpd= isToHighSpeed(speed) ? Math.abs(Math.abs(speed) - Math.abs(properties.spdMax())):0;
        //double rewardFail = isFail ? properties.rewardFail()*(0+devSpd/10) : 0d;
        boolean isSuccess = isLanded(y) && !isToHighSpeed(speed);
        double rewardSuccess= isSuccess? -properties.rewardFail(): 0d;
        double rewardFail = isFail ? properties.rewardFail()*(1+0*devSpd/10) : 0d;
        double rewardPosAcc= MyMathUtils.isPos(acc) ? -Math.abs(acc) : 0d;
        return properties.rewardStep() + rewardFail+rewardSuccess; //+rewardPosAcc;
    }

    private boolean isToHighSpeed(double speed) {
        return Math.abs(speed) > Math.abs(properties.spdMax());
    }


    private double calculateRewardOld(double speed, boolean isFail) {
        double devSpd= isToHighSpeed(speed) ? Math.abs(Math.abs(speed) - Math.abs(properties.spdMax())):0;
        //double rewardFail = isFail ? properties.rewardFail()*(0+devSpd/10) : 0d;
        double rewardFail = isFail ? properties.rewardFail()*(0.1+devSpd/10) : 0d;
        return properties.rewardStep() + rewardFail;
    }

    public double calculateAcceleration(double action) {
        double m = properties.massLander();
        double forceInNewton=getForceInNewton(action);
        double force= MathUtils.clip(forceInNewton, -properties.forceMax(), properties.forceMax());
        return (force - m * properties.g()) / m;
    }

    public double getForceInKiloNewton(double forceInNewton) {
        return MyUnitConverter.convertForce(forceInNewton, Units.NEWTON, NonSIUnits.KILO_NEWTON);
    }

    public double getForceInNewton(double forceInKiloNewton) {
        return MyUnitConverter.convertForce(forceInKiloNewton, NonSIUnits.KILO_NEWTON, Units.NEWTON);
    }



}
