package mcts_spacegame.models_battery_cell;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import mcts_spacegame.environment.StepReturnGeneric;
import mcts_spacegame.generic_interfaces.StateInterface;


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
        return newWithVariables(variables.copy());
    }

    @Override
    public void setFromReturn(StepReturnGeneric<CellVariables> stepReturn) {
        variables=stepReturn.copyState().getVariables();
    }
}
