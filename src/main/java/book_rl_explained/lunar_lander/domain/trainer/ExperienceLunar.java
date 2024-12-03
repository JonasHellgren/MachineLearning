package book_rl_explained.lunar_lander.domain.trainer;

import book_rl_explained.lunar_lander.domain.environment.StateLunar;
import book_rl_explained.lunar_lander.domain.environment.StepReturnLunar;

public record ExperienceLunar(
        StateLunar state,
        double action,
        double reward,
        StateLunar stateNew

) {
    public static ExperienceLunar of(StateLunar state,
                                     double action,
                                     double reward,
                                     StateLunar stateNew) {
        return new ExperienceLunar(state, action, reward, stateNew);
    }
}
