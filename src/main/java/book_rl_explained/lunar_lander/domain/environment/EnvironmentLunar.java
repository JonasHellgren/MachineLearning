package book_rl_explained.lunar_lander.domain.environment;

import common.math.MathUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
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

    @Override
    public StepReturnLunar step(StateLunar state, double action) {
        double speed0 = state.variables.spd();
        double y0 = state.variables.y();
        double acc = calculateAcceleration(action);
        double speed = speed0 + acc * properties.dt();
        double y = y0 + speed * properties.dt();

        var stateNew = StateLunar.of(y, speed);
        double yFarOf = properties.yMax();
        boolean isTerminal = y < properties.ySurface() || y > yFarOf;
        boolean isFail = isTerminal && speed < -properties.spdMax() || y > yFarOf;
        //double rewardFail = isFail ? props.rewardFail() : 0d;
        double devSpd=Math.abs(speed) > Math.abs(properties.spdMax()) ? Math.abs(Math.abs(speed) - Math.abs(properties.spdMax())):0;
        double rewardFail = isFail ? properties.rewardFail()*(0+devSpd/10) : 0d;
        double reward = properties.rewardStep() + rewardFail;

        return StepReturnLunar.builder()
                .stateNew(stateNew)
                .isFail(isFail)
                .isTerminal(isTerminal)
                .reward(reward)
                .build();
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
