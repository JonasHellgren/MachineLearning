package monte_carlo_tree_search.domains.elevator;

import common.Conditionals;
import common.MathUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import monte_carlo_tree_search.classes.StepReturnGeneric;
import monte_carlo_tree_search.generic_interfaces.StateInterface;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

@ToString
@Getter
@EqualsAndHashCode
public class StateElevator implements StateInterface<ElevatorVariables> {
    public static final double SOE_MIN = 0.1;
    public static final double SOE_MAX = 1.0;
    public static final int MAX_POS=30;
    public static final int NOF_FLOORS=3;
    public static final int MAX_NOF_PERSONS_IN_ELEVATOR=10;
    public static final int MAX_NOF_PERSONS_WAITING=3;

    static Predicate<Integer> isValidPos = pos -> pos>=0 && pos <= MAX_POS;
    static Predicate<Integer> isValidPersonsInElevator = n -> n>=0 && n <= MAX_NOF_PERSONS_IN_ELEVATOR;
    static Predicate<List<Integer>> isValidPersonsWaiting = list -> list.size()==NOF_FLOORS;
    static Predicate<Double> isValidSoE= soe -> soe>=SOE_MIN && soe <= SOE_MAX;

    ElevatorVariables variables;

    private StateElevator(ElevatorVariables variables) {
        this.variables = variables;
    }
    
    public static StateElevator newFromVariables(ElevatorVariables variables) {
        boolean isValid=isVariablesValid(variables);
        return new StateElevator(variables);
    }
    
    public static StateElevator newFullyChargedReadyAtBottomFloorNoPassengers() {
        return new StateElevator(ElevatorVariables.builder()
                .pos(0)
                .nPersonsInElevator(0)
                .nPersonsWaiting(Arrays.asList(0,0,0))
                .SoE(SOE_MAX)
                .build());
    }
    
    public static boolean isVariablesValid(ElevatorVariables variables) {

        boolean isValid = isValidPos.test(variables.pos) &&
                isValidPersonsInElevator.test(variables.nPersonsInElevator) &&
                isValidPersonsWaiting.test(variables.nPersonsWaiting) &&
                isValidSoE.test(variables.SoE);

        if (!isValid) {
            throw new IllegalArgumentException(getErrorMessage(variables));
        }
        return isValid;
        
    }

    @NotNull
    private static String getErrorMessage(ElevatorVariables variables) {
        return "isValidPos = "+isValidPos.test(variables.pos)+
                ", isValidPersonsInElevator = "+isValidPersonsInElevator.test(variables.nPersonsInElevator)+
                ", isValidPersonsWaiting = "+isValidPersonsWaiting.test(variables.nPersonsWaiting)+
                ", isValidSoE = "+isValidSoE.test(variables.SoE);
    }

    @Override
    public StateInterface<ElevatorVariables> copy() {
        return newFromVariables(variables.copy());
    }

    @Override
    public void setFromReturn(StepReturnGeneric<ElevatorVariables> stepReturn) {
        variables=stepReturn.copyState().getVariables();
    }

    @Override
    public String toString() {
        return variables.toString();
    }

}
