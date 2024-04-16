package safe_rl.environments.buying_electricity;

import lombok.AllArgsConstructor;
import safe_rl.domain.abstract_classes.StateI;

@AllArgsConstructor
public class StateBuying implements StateI<VariablesBuying> {

    VariablesBuying variables;

    public static StateBuying  newDefault() {
        return new StateBuying(VariablesBuying.newDefault());
    }

    @Override
    public VariablesBuying getVariables() {
        return variables;
    }

    @Override
    public void setVariables(VariablesBuying variables) {
        this.variables=variables;
    }

    @Override
    public StateI<VariablesBuying> copy() {
        return new StateBuying(variables);
    }
}
