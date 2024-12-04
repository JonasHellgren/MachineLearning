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

    LunarProperties props;

    public static EnvironmentLunar createDefault() {
        return new EnvironmentLunar(LunarProperties.defaultProps());
    }

    @Override
    public StepReturnLunar step(StateLunar state, double action) {
        double speed0 = state.variables.spd();
        double y0 = state.variables.y();
        double m = props.massLander();
        double forceInNewton=getForceInNewton(action);
        double force= MathUtils.clip(forceInNewton, -props.forceMax(), props.forceMax());
        double acc = (force - m * props.g()) / m;
        double speed = speed0 + acc * props.dt();
        double y = y0 + speed * props.dt();

        var stateNew = StateLunar.of(y, speed);
        boolean isTerminal = y < props.ySurface();
        boolean isFail = isTerminal && speed < -props.spdMax();
        double rewardFail = isFail ? props.rewardFail() : 0d;
        double reward = props.rewardStep() + rewardFail;

        return StepReturnLunar.builder()
                .stateNew(stateNew)
                .isFail(isFail)
                .isTerminal(isTerminal)
                .reward(reward)
                .build();
    }

    public double getForceInKiloNewton(double forceInNewton) {
        return MyUnitConverter.convertForce(forceInNewton, Units.NEWTON, NonSIUnits.KILO_NEWTON);
    }

    public double getForceInNewton(double forceInKiloNewton) {
        return MyUnitConverter.convertForce(forceInKiloNewton, NonSIUnits.KILO_NEWTON, Units.NEWTON);
    }



}
