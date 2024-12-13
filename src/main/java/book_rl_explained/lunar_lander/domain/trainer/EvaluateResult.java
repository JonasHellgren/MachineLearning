package book_rl_explained.lunar_lander.domain.trainer;


import book_rl_explained.lunar_lander.domain.environment.StateLunar;

public record EvaluateResult(
        Double sumRewardsNSteps,
        StateLunar stateFuture,
        boolean isFutureStateOutside,
        boolean isFutureTerminal) {

    public boolean isFutureOutsideOrTerminal() {
        //return isFutureTerminal || isFutureStateOutside;
        return isFutureStateOutside;
    }
}
