package policy_gradient_problems.the_problems.short_corridor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import policy_gradient_problems.abstract_classes.StateI;
import policy_gradient_problems.the_problems.sink_the_ship.VariablesShip;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class StateSC implements StateI<VariablesSC> {

    VariablesSC variables;

    public static StateSC newDefault() {
        return new StateSC(VariablesSC.newDefault());
    }
    public static StateSC newFromPos(int pos) {
        return new StateSC(new VariablesSC(pos));
    }

    public int getPos() {
        return variables.pos();
    }

    @Override
    public StateI<VariablesSC> copy() {
        return StateSC.newFromPos(getPos());
    }

    @Override
    public List<Double> asList() {
        return List.of((double) getPos());
    }
}
