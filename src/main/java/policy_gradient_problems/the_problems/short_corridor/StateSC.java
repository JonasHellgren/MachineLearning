package policy_gradient_problems.the_problems.short_corridor;

import lombok.*;
import org.apache.commons.math3.linear.RealVector;
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
    public static StateSC randomNonTerminal() {
        return new StateSC(new VariablesSC(AgentParamActorSCHelper.getRandomNonTerminalState()));
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

    @SneakyThrows
    @Override
    public RealVector asRealVector() {
        throw new NoSuchMethodException();
    }
}
