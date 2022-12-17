package mcts_spacegame.models_battery_cell;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Builder
@ToString
@EqualsAndHashCode
public class StateCell implements StateInterface<StateCell> {

    public double SoC;
    public double temperature;
    public double time;

    @Override
    public StateInterface<StateCell> copy() {
        return StateCell.builder()
                .SoC(SoC).temperature(temperature).time(time).build();
    }

    /*
    @Override
    public void setFromReturn(StepReturnGeneric<StateCell> stepReturn) {
        this.SoC=stepReturn.newState.SoC;
        this.temperature=stepReturn.newState.temperature;
        this.time=stepReturn.newState.time;
    }  */

}
