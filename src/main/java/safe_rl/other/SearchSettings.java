package safe_rl.other;

import lombok.Builder;

@Builder
public record SearchSettings(
        double xMin, double xMax, double tol
) {
}
