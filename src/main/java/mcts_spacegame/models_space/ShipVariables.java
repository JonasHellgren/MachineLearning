package mcts_spacegame.models_space;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Builder
@ToString
@EqualsAndHashCode
public class ShipVariables {
    public Integer x,y;

    public ShipVariables copy() {
        return ShipVariables.builder().x(x).y(y).build();
    }
}
