package book_rl_explained.lunar_lander.domain.trainer;

import book_rl_explained.lunar_lander.domain.environment.StateLunar;
import book_rl_explained.lunar_lander.domain.environment.StepReturnLunar;

/**
 * Represents a single experience in the lunar lander environment.
 * An experience consists of a state, an action taken in that state,
 * and the resulting step return.
 */
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

    public boolean isTransitionToTerminal() {
        return stepReturn.isTerminal();
    }

    public boolean isTransitionToFail() {
        return stepReturn.isFail();
    }

    public StateLunar stateNew() {
        return stepReturn.stateNew();
    }


}
