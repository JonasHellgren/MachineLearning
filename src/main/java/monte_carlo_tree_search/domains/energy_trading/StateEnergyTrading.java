package monte_carlo_tree_search.domains.energy_trading;

import lombok.Getter;
import monte_carlo_tree_search.classes.StepReturnGeneric;
import monte_carlo_tree_search.interfaces.StateInterface;
import org.jetbrains.annotations.NotNull;
import java.util.function.Predicate;

@Getter
public class StateEnergyTrading implements StateInterface<VariablesEnergyTrading> {

    VariablesEnergyTrading variables;

    static Predicate<Integer> isValidTime =
            time -> time>=EnvironmentEnergyTrading.MIN_TIME &&
            time <= EnvironmentEnergyTrading.AFTER_MAX_TIME;

    StateEnergyTrading(VariablesEnergyTrading variables) {
        this.variables = variables;
    }

    public static StateEnergyTrading newFromVariables(VariablesEnergyTrading variables) {
        if (!isVariablesValid(variables)) {
            System.out.println("variables = " + variables);
            throw new IllegalArgumentException(getErrorMessage(variables));
        }
        return new StateEnergyTrading(variables);
    }

    public static StateEnergyTrading newDefault() {
        return StateEnergyTrading.newFromVariables(VariablesEnergyTrading.newDefault());
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

    public static boolean isVariablesValid(VariablesEnergyTrading variables) {
        return isValidTime.test(variables.time);
    }

    @NotNull
    private static String getErrorMessage(VariablesEnergyTrading variables) {
        return "isValidTime = "+isValidTime.test(variables.time);
    }

}
