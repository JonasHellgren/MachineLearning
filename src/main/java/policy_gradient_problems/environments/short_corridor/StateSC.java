package policy_gradient_problems.environments.short_corridor;

import lombok.*;
import org.apache.commons.math3.linear.RealVector;
import policy_gradient_problems.domain.abstract_classes.StateI;

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

    public static StateSC newFromRealPos(int pos) {
        return new StateSC(VariablesSC.newOfReal(pos));
    }

    public static StateSC newFromObsPos(int pos) {
        return new StateSC(VariablesSC.newOfObserved(pos));
    }


    public static StateSC randomNonTerminal() {
        return new StateSC(VariablesSC.newOfReal(EnvironmentSC.getRandomNonTerminalState()));
    }

    public int getRealPos() {
        return variables.posReal();
    }

    public int getObsPos() {
        return variables.posObserved();
    }

    @Override
    public StateI<VariablesSC> copy() {
        return StateSC.newFromRealPos(getRealPos());
    }

    @Override
    public List<Double> asList() {
        return List.of((double) getObsPos());
    }

    @SneakyThrows
    @Override
    public RealVector asRealVector() {
        throw new NoSuchMethodException();
    }

/*    public StateSC asObserved() {
        int realPos= EnvironmentSC.getObservedPos(variables.posReal());
        return newFromRealPos(realPos);
    }*/

}
