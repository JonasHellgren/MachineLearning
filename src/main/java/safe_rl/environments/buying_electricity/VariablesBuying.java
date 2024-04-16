package safe_rl.environments.buying_electricity;

import lombok.Builder;

@Builder
public record VariablesBuying (
        double time,
        double soc,
        double socStart
) {

    public static VariablesBuying newZero() {
        return new VariablesBuying(0d,0.0,0.0);
    }

}
