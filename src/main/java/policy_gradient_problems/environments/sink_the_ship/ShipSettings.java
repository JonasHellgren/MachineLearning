package policy_gradient_problems.environments.sink_the_ship;

import lombok.Builder;
import lombok.With;

@Builder
public record ShipSettings (
        int nStates,
        int nFeatures,
        @With  double devMaxMeter
) {


    public static ShipSettings newDefault() {
        return ShipSettings.builder().nStates(2).nFeatures(1).devMaxMeter(50).build();
    }
}
