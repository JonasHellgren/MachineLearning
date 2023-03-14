package monte_carlo_tree_search.domains.energy_trading;

import lombok.Getter;
import monte_carlo_tree_search.classes.StepReturnGeneric;
import monte_carlo_tree_search.interfaces.StateInterface;

@Getter
public class StateEnergyTrading implements StateInterface<VariablesEnergyTrading> {


    VariablesEnergyTrading variables;

    StateEnergyTrading(VariablesEnergyTrading variables) {
        this.variables = variables;
    }

    public static StateEnergyTrading newFromVariables(VariablesEnergyTrading variables) {
        return new StateEnergyTrading(variables);
    }


    @Override
    public StateInterface<VariablesEnergyTrading> copy() {
        return newFromVariables(variables.copy());
    }

    @Override
    public void setFromReturn(StepReturnGeneric<VariablesEnergyTrading> stepReturn) {
        variables=stepReturn.copyState().getVariables();
    }

    public String toString() {
        return variables.toString();
    }

}
