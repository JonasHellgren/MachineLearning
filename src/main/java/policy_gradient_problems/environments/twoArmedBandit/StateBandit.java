package policy_gradient_problems.environments.twoArmedBandit;

import lombok.*;
import org.apache.commons.math3.linear.RealVector;
import policy_gradient_problems.domain.abstract_classes.StateI;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class StateBandit implements StateI<VariablesBandit> {

    VariablesBandit variables;

    public static StateBandit newDefault() {
        return new StateBandit(VariablesBandit.newDefault());
    }

    @Override
    public StateI<VariablesBandit> copy() {
        StateBandit stateBandit = StateBandit.newDefault();
        stateBandit.setVariables(variables);
        return stateBandit;
    }

    @Override
    public List<Double> asList() {
        return List.of((double) variables.arm());
    }

    @SneakyThrows
    @Override
    public RealVector asRealVector() {
        throw new NoSuchMethodException();
    }
}
