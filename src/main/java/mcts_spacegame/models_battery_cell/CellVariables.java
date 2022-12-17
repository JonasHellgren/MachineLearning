package mcts_spacegame.models_battery_cell;

import lombok.Builder;
import lombok.ToString;

@Builder
@ToString
public class CellVariables {
    public double SoC;
    public double temperature;
    public double time;

    public CellVariables copy() {
        return CellVariables.builder()
                .SoC(SoC).temperature(temperature).time(time).build();
    }

}
