package monte_carlo_tree_search.domains.battery_cell;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import monte_carlo_tree_search.models_and_support_classes.StepReturnGeneric;
import monte_carlo_tree_search.interfaces.StateInterface;


@ToString
@Getter
@EqualsAndHashCode
public class StateCell implements StateInterface<CellVariables> {

    CellVariables variables;

    public static StateCell newStateFromSoCTempAndTime(double SoC, double temperature, double time) {
        return new StateCell(CellVariables.builder()
                .SoC(SoC).temperature(temperature).time(time).build());
    }

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
