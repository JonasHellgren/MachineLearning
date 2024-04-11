package policy_gradient_problems.environments.sink_the_ship;

import lombok.Builder;

@Builder
public record ShipSettings(
        int nStates

) {


    public static ShipSettings newDefault() {

        return ShipSettings.builder().nStates(2).build();

    }
}
