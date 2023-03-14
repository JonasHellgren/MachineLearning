package monte_carlo_tree_search.interfaces;

import monte_carlo_tree_search.domains.elevator.ActionElevator;
import monte_carlo_tree_search.domains.energy_trading.ActionEnergyTrading;

import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class ActionIntegerAbstract implements ActionInterface<Integer> {

    protected int minActionValue;
    protected int maxActionValue;
    protected Integer actionValue;

    public ActionIntegerAbstract(int minAction, int maxAction, int value) {
        this.minActionValue=minAction;
        this.maxActionValue=maxAction;
        this.actionValue=value;
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
        Predicate<Integer> isApplicAction = a -> a>=minActionValue && a <= maxActionValue;
        Predicate<Integer> isNonApplicAction = a -> a.equals(nonApplicableAction());
        return isApplicAction.or(isNonApplicAction).test(actionValue);
    }




}
