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
        return StateSC.newFromRealPos(getPos());
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

    public StateSC asObserved() {
        int obsPos= EnvironmentSC.getObservedPos(this);
        return newFromRealPos(obsPos);
    }

}
