package policy_gradient_problems.the_problems.twoArmedBandit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import policy_gradient_problems.abstract_classes.StateI;

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
}
