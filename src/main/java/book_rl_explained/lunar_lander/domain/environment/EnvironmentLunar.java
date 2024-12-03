package book_rl_explained.lunar_lander.domain.environment;

import common.math.MathUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

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
        double force= MathUtils.clip(action, -props.forceMax(), props.forceMax());
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
}
