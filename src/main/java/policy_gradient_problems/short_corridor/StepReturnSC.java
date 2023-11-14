package policy_gradient_problems.short_corridor;

public record StepReturnSC(int state, double reward) {

    public static StepReturnSC of(int state, double reward) {
        return new StepReturnSC(state,reward);
    }

}
