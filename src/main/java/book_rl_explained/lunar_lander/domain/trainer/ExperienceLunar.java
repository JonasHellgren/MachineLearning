package book_rl_explained.lunar_lander.domain.trainer;

import book_rl_explained.lunar_lander.domain.environment.StateLunar;
import book_rl_explained.lunar_lander.domain.environment.StepReturnLunar;

public record ExperienceLunar(
        StateLunar state,
        double action,
        StepReturnLunar stepReturn

) {
    public static ExperienceLunar of(StateLunar state,
                                     double action,
                                     StepReturnLunar sr) {
        return new ExperienceLunar(state, action, sr);
    }

    public double reward() {
        return stepReturn.reward();
    }

    public boolean isTerminal() {
        return stepReturn.isTerminal();
    }

    public boolean isFail() {
        return stepReturn.isFail();
    }

    public StateLunar stateNew() {
        return stepReturn.stateNew();
    }


}
