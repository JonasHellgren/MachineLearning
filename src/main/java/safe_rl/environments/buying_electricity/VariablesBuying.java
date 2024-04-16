package safe_rl.environments.buying_electricity;

import lombok.Builder;
import lombok.With;

@Builder
public record VariablesBuying (
        double time,
        @With double soc,
        @With double socStart
) {

    public static VariablesBuying newZero() {
        return new VariablesBuying(0d,0.0,0.0);
    }

    public static VariablesBuying newSoc(double soc) {
        return new VariablesBuying(0d,soc,soc);
    }

}
