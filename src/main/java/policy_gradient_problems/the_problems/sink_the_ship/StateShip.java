package policy_gradient_problems.the_problems.sink_the_ship;

import lombok.*;
import org.apache.commons.math3.linear.RealVector;
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

    public int getPos() {
        return variables.pos();
    }

    @Override
    public StateI<VariablesShip> copy() {
        return StateShip.newFromPos(getPos());
    }

    @Override
    public List<Double> asList() {
        return List.of((double) getPos());
    }

    @SneakyThrows
    @Override
    public RealVector asRealVector() {
        throw new NoSuchMethodException();
    }
}
