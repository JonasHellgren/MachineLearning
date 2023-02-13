package monte_carlo_tree_search.domains.elevator;

import lombok.Builder;
import lombok.ToString;
import lombok.extern.java.Log;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;

import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Builder
@Log
@ToString
public class ActionElevator implements ActionInterface<Integer> {

    public static final int MIN_ACTION_DEFAULT = -1;
    public static final int MAX_ACTION_DEFAULT = 1;
    public static final int ACTION_DEFAULT = 0;
    @Builder.Default
    int minActionValue = MIN_ACTION_DEFAULT;
    @Builder.Default
    int maxActionValue = MAX_ACTION_DEFAULT;
    @Builder.Default
    private Integer actionValue= ACTION_DEFAULT;

    public static ActionElevator newValueDefaultRange(Integer actionValue) {
        ActionElevator actionElevator=ActionElevator.builder().actionValue(actionValue).build();
        actionElevator.throwIfNotValid(actionValue);
        return actionElevator;
    }

    public static ActionElevator newValueSpecficRange(Integer actionValue,
                                                      Integer minActionValue,
                                                      Integer maxActionValue) {
        ActionElevator actionElevator=ActionElevator.builder()
                .actionValue(actionValue).minActionValue(minActionValue).maxActionValue(maxActionValue)
                .build();
        actionElevator.throwIfNotValid(actionValue);
        return actionElevator;
    }

    @Override
    public void setValue(Integer actionValue) {
        throwIfNotValid(actionValue);
        this.actionValue=actionValue;
    }

    private void throwIfNotValid(Integer actionValue) {
        if (!isValid(actionValue)) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public Integer getValue() {
        return actionValue;
    }

    @Override
    public ActionInterface<Integer> copy() {
        return ActionElevator.builder()
                .minActionValue(minActionValue)
                .maxActionValue(maxActionValue)
                .actionValue(actionValue)
                .build();
    }

    @Override
    public Set<Integer> applicableActions() {
        return IntStream.rangeClosed(minActionValue, maxActionValue)  //inclusive end
                .boxed()
                .collect(Collectors.toSet());
    }

    @Override
    public Integer nonApplicableAction() {
        return minActionValue-1;
    }

    public boolean isValid(Integer actionValue) {
        Predicate<Integer> isValidAction = a -> a>=minActionValue && a <= maxActionValue;
        return isValidAction.test(actionValue);
    }

}
