package policy_gradient_problems.environments.short_corridor;


public record VariablesSC(int pos) {

    public static VariablesSC newDefault() {
        return new VariablesSC(0);
    }

    public VariablesSC copy() {
        return new VariablesSC(pos);
    }
}
