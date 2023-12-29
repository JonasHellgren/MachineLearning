package policy_gradient_problems.the_problems.sink_the_ship;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import policy_gradient_problems.abstract_classes.StateI;
import policy_gradient_problems.the_problems.twoArmedBandit.VariablesBandit;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class StateShip implements StateI<VariablesShip> {

    VariablesShip variables;

    public static StateShip newDefault() {
        return new StateShip(VariablesShip.newDefault());
    }


    public static StateShip newFromPos(int pos) {
        return new StateShip(new VariablesShip(pos));
    }


    @Override
    public StateI<VariablesShip> copy() {
        StateShip stateShip = StateShip.newDefault();
        stateShip.setVariables(variables);
        return stateShip;
    }

    @Override
    public List<Double> asList() {
        return List.of((double) variables.pos());
    }
}
