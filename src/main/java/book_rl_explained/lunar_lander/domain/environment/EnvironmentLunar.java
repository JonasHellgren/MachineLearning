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
        double dt = properties.dt();
        double speed0 = state.variables.spd();
        double y0 = state.variables.y();
        double acc = calculateAcceleration(action);
        double speed = speed0 + acc * dt;
        double y = y0 + speed * dt;
        var stateNew = StateLunar.of(y, speed);
        boolean isTerminal = isLanded(y) || isToHighPosition(y);
        boolean isFail = isLanded(y)  && isToHighSpeed(speed) || isToHighPosition(y);
        double reward = calculateReward(y, speed, isFail);

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

    private boolean isToHighPosition(double y) {
        return y > properties.yMax();
    }


    private double calculateReward(double y, double speed,  boolean isFail) {
        boolean isSuccess = isLanded(y) && !isToHighSpeed(speed);
        double rewardSuccess = isSuccess ? properties.rewardSuccess() : 0d;
        double rewardFail = isFail ? properties.rewardFail() : 0d;
        return properties.rewardStep() + rewardFail + rewardSuccess;
    }

    private boolean isToHighSpeed(double speed) {
        return Math.abs(speed) > Math.abs(properties.spdLimitCrash());
    }

    public double calculateAcceleration(double action) {
        double m = properties.massLander();
        double forceInNewton = getForceInNewton(action);
        double force = MathUtils.clip(forceInNewton, -properties.forceMax(), properties.forceMax());
        return (force - m * properties.g()) / m;
    }

    public double getForceInKiloNewton(double forceInNewton) {
        return MyUnitConverter.convertForce(forceInNewton, Units.NEWTON, NonSIUnits.KILO_NEWTON);
    }

    public double getForceInNewton(double forceInKiloNewton) {
        return MyUnitConverter.convertForce(forceInKiloNewton, NonSIUnits.KILO_NEWTON, Units.NEWTON);
    }


}
