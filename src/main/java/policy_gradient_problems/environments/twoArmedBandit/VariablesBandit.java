package policy_gradient_problems.environments.twoArmedBandit;

public record VariablesBandit(int arm) {

    public static VariablesBandit newDefault() {
        return new VariablesBandit(0);
    }

    public VariablesBandit copy() {
        return new VariablesBandit(arm);
    }
}
