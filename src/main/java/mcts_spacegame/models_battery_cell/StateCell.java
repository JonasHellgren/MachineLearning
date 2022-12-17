package mcts_spacegame.models_battery_cell;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import mcts_spacegame.models_space.State;


@ToString
@Getter
@EqualsAndHashCode
public class StateCell implements StateInterface<CellVariables> {

    CellVariables variables;

    public StateCell(CellVariables variables) {
        this.variables = variables;
    }

    public static StateCell newDefault() {
        return new StateCell(CellVariables.builder().build());
    }

    public static StateCell newWithVariables(CellVariables variables) {
        return new StateCell(variables);
    }

    @Override
    public StateCell copy() {
        return new StateCell(variables.copy());
    }

    /*
    @Override
    public void setFromReturn(StepReturnGeneric<StateCell> stepReturn) {
        this.SoC=stepReturn.newState.SoC;
        this.temperature=stepReturn.newState.temperature;
        this.time=stepReturn.newState.time;
    }  */

}
