package safe_rl.other.capacity_search;

import lombok.Builder;

@Builder
public record SearchSettings(
        double xMin, double xMax, double tol, int nIterMax
) {
}
