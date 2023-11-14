package policy_gradient_problems.short_corridor;

public record StepReturnSC(int state, int observedState, boolean isTerminal, double reward) {

    public static StepReturnSC of(int state, int observedState, boolean isTerminal, double reward) {
        return new StepReturnSC(state,observedState,isTerminal, reward);
    }

}
