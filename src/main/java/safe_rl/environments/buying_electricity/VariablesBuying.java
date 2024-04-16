package safe_rl.environments.buying_electricity;

public record VariablesBuying (
        int time,
        double soc
) {

    public static VariablesBuying newDefault() {
        return new VariablesBuying(0,0.5);
    }

}
