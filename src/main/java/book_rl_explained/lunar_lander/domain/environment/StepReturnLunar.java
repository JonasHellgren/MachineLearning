package book_rl_explained.lunar_lander.domain.environment;

import common.other.Counter;
import lombok.Builder;


@Builder
public record StepReturnLunar(
        StateLunar stateNew,
        boolean isFail,
        boolean isTerminal,
        double reward
) {
    public static StepReturnLunar ofNotFailAndNotTerminal() {
        return new StepReturnLunar(null, false, false, 0.0);
    }


    public static StepReturnLunar ofNotFail(StateLunar stateNew,  boolean isTerminal, double reward) {
        return new StepReturnLunar(stateNew, false, isTerminal, reward);
    }

    public static StepReturnLunar of(StateLunar stateNew, boolean isFail, boolean isTerminal, double reward) {
        return new StepReturnLunar(stateNew, isFail, isTerminal, reward);
    }


    public boolean isNotTerminalAndNofStepsNotExceeded(Counter counter) {
        return !isTerminal && !counter.isExceeded();
    }
}
